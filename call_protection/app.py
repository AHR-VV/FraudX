from flask import Flask, jsonify, request, send_from_directory
from datetime import datetime, timezone
import json
import os

app = Flask(__name__)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
FRONTEND_DIR = os.path.join(BASE_DIR, "frontend")
DATABASE_FILE = os.path.join(BASE_DIR, "database", "reported_numbers.json")


@app.route("/")
def index():
    return send_from_directory(FRONTEND_DIR, "index.html")


def load_database():
    try:
        with open(DATABASE_FILE, "r") as file:
            records = json.load(file)

        if not isinstance(records, list):
            return None

        return records

    except (FileNotFoundError, json.JSONDecodeError):
        return None


def save_database(records):
    with open(DATABASE_FILE, "w") as file:
        json.dump(records, file, indent=4)


@app.route("/api/check-number", methods=["GET"])
def check_number():
    phone = request.args.get("phone", "").strip()

    if not phone.isdigit() or len(phone) != 10:
        return jsonify({
            "error": "Please enter a valid 10-digit phone number."
        }), 400

    records = load_database()

    if records is None:
        return jsonify({
            "error": "Database is unavailable or invalid."
        }), 500

    matching_records = [
        record for record in records
        if record.get("phone") == phone
    ]

    if matching_records:
        total_reports = sum(
            record.get("reports", 1)
            if type(record.get("reports", 1)) is int else 1
            for record in matching_records
        )

        categories = list(dict.fromkeys(
            record.get("category", "Unspecified")
            for record in matching_records
        ))

        report_dates = [
            {
                "date": record.get("submitted_at"),
                "category": record.get("category", "Unspecified"),
                "status": record.get(
                    "status",
                    "Unverified - Under Review"
                )
            }
            for record in matching_records
        ]

        return jsonify({
            "found": True,
            "result": {
                "category": ", ".join(categories),
                "reports": total_reports,
                "status": "Unverified - Under Review",
                "dates": report_dates
            },
            "message": (
                "These are community-submitted reports. "
                "They have not been independently verified. "
                "A report does not by itself prove fraud."
            )
        })

    return jsonify({
        "found": False,
        "message": (
            "UNKNOWN NUMBER - No reports found in our database. "
            "This does not guarantee that the number is safe."
        )
    })


@app.route("/api/report-number", methods=["POST"])
def report_number():
    data = request.get_json(silent=True) or {}

    phone = str(data.get("phone", "")).strip()
    category = str(data.get("category", "")).strip()
    description = str(data.get("description", "")).strip()

    allowed_categories = [
        "Fake Lottery",
        "Impersonation",
        "Bank Fraud",
        "Suspicious Call",
        "Other"
    ]

    if not phone.isdigit() or len(phone) != 10:
        return jsonify({
            "error": "Enter a valid 10-digit phone number."
        }), 400

    if category not in allowed_categories:
        return jsonify({
            "error": "Select a valid category."
        }), 400

    if len(description) < 10 or len(description) > 500:
        return jsonify({
            "error": "Description must be between 10 and 500 characters."
        }), 400

    records = load_database()

    if records is None:
        return jsonify({
            "error": "Database is unavailable or invalid."
        }), 500

    new_record = {
        "phone": phone,
        "category": category,
        "reports": 1,
        "status": "Unverified - Under Review",
        "description": description,
        "submitted_at": datetime.now(timezone.utc).isoformat(
            timespec="seconds"
        )
    }

    records.append(new_record)

    try:
        save_database(records)
    except OSError:
        return jsonify({
            "error": "Unable to save the report."
        }), 500

    return jsonify({
        "success": True,
        "message": (
            "Report submitted successfully. "
            "It is awaiting review."
        )
    }), 201


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 5000)), debug=False)
