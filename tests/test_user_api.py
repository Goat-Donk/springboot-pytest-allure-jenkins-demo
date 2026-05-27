import allure
import pytest

from tests.utils.data_loader import load_json, render_payload


create_user_cases = load_json("data/create_user_cases.json")


@allure.feature("User API")
@allure.story("Health Check")
def test_health(base_url, session):
    response = session.get(f"{base_url}/health", timeout=5)

    assert response.status_code == 200
    body = response.json()
    assert body["code"] == 0
    assert body["data"]["status"] == "UP"


@allure.feature("User API")
@allure.story("Seed Data")
def test_list_seed_users(base_url, session):
    response = session.get(f"{base_url}/users", timeout=5)

    assert response.status_code == 200
    body = response.json()
    assert body["code"] == 0
    assert len(body["data"]) >= 2


@allure.feature("User API")
@allure.story("Create User")
@pytest.mark.parametrize("case_data", create_user_cases, ids=[case["case_name"] for case in create_user_cases])
def test_create_user_data_driven(base_url, session, unique_suffix, case_data):
    payload = render_payload(case_data["payload"], unique_suffix)
    allure.dynamic.title(case_data["case_name"])
    allure.attach(str(payload), name="request_payload", attachment_type=allure.attachment_type.TEXT)

    response = session.post(f"{base_url}/users", json=payload, timeout=5)
    body = response.json()
    allure.attach(response.text, name="response_body", attachment_type=allure.attachment_type.JSON)

    assert response.status_code == case_data["expected_status"]
    assert body["code"] == case_data["expected_code"]
    if "expected_message" in case_data:
        assert body["message"] == case_data["expected_message"]
    if "expected_message_contains" in case_data:
        assert case_data["expected_message_contains"] in body["message"]


@allure.feature("User API")
@allure.story("Update User")
def test_update_user(base_url, session, unique_suffix):
    create_payload = {
        "username": f"update_before_{unique_suffix}",
        "email": f"update_before_{unique_suffix}@example.com",
        "age": 31,
        "city": "Nanjing"
    }
    create_response = session.post(f"{base_url}/users", json=create_payload, timeout=5)
    user_id = create_response.json()["data"]["id"]

    update_payload = {
        "username": f"update_after_{unique_suffix}",
        "email": f"update_after_{unique_suffix}@example.com",
        "age": 32,
        "city": "Suzhou"
    }
    response = session.put(f"{base_url}/users/{user_id}", json=update_payload, timeout=5)

    assert response.status_code == 200
    body = response.json()
    assert body["code"] == 0
    assert body["data"]["username"] == update_payload["username"]
    assert body["data"]["city"] == update_payload["city"]


@allure.feature("User API")
@allure.story("Delete User")
def test_delete_user(base_url, session, unique_suffix):
    create_payload = {
        "username": f"delete_{unique_suffix}",
        "email": f"delete_{unique_suffix}@example.com",
        "age": 24,
        "city": "Wuhan"
    }
    create_response = session.post(f"{base_url}/users", json=create_payload, timeout=5)
    user_id = create_response.json()["data"]["id"]

    delete_response = session.delete(f"{base_url}/users/{user_id}", timeout=5)
    assert delete_response.status_code == 200
    assert delete_response.json()["code"] == 0

    get_response = session.get(f"{base_url}/users/{user_id}", timeout=5)
    assert get_response.status_code == 400
    assert "user not found" in get_response.json()["message"]
