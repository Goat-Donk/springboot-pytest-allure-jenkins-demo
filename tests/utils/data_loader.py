import json
from pathlib import Path


def load_json(relative_path: str):
    data_file = Path(__file__).resolve().parents[1] / relative_path
    return json.loads(data_file.read_text(encoding="utf-8"))


def render_payload(template: dict, suffix: str):
    payload = {}
    for key, value in template.items():
        if isinstance(value, str):
            payload[key] = value.replace("{suffix}", suffix)
        else:
            payload[key] = value
    return payload
