import os
import time

import pytest
import requests


@pytest.fixture(scope="session")
def base_url():
    return os.getenv("BASE_URL", "http://127.0.0.1:18080/api")


@pytest.fixture(scope="session")
def session():
    client = requests.Session()
    yield client
    client.close()


@pytest.fixture
def unique_suffix():
    return str(int(time.time() * 1000))
