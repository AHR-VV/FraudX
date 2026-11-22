# FraudX – Call Protection Module

## 1. Project Overview

The Call Protection Module is a component of the FraudX platform designed to help users identify potentially suspicious phone numbers.

It allows users to check whether a phone number has previously been reported for suspicious activity using the available database.

## 2. Problem Statement

Phone-based fraud and suspicious calls can create risks for individuals. Users may not know whether a phone number has previously been reported.

This module aims to provide a simple way to check reported phone numbers and make users more aware of potentially suspicious activity.

## 3. Objectives

- Provide a simple phone number checking interface.
- Identify numbers recorded in the reported numbers database.
- Display appropriate results to users.
- Maintain reported number records using a database file.
- Integrate Call Protection into the main FraudX platform.

## 4. Key Features

- Phone number input.
- Check Number functionality.
- Search against stored reported number records.
- Display results based on database matches.
- Simple and user-friendly interface.

## 5. Technologies Used

- Python – Backend development.
- HTML – Frontend interface.
- JSON – Database storage.
- Flask – Web application framework.

## 6. Project Structure

call_protection/
│
├── app.py
├── README.md
├── requirements.txt
│
├── database/
│   └── reported_numbers.json
│
└── frontend/
    └── index.html

## 7. Database Description

The module uses a JSON file:

database/reported_numbers.json

Purpose:
- Store reported phone number records.
- Provide data for number checking.
- Support lookup operations.

The database currently uses JSON-based storage.

## 8. Working Procedure

1. The user opens the Call Protection interface.
2. The user enters a phone number.
3. The user clicks the Check Number button.
4. The backend processes the request.
5. The number is checked against the available database records.
6. The result is displayed on the interface.

## 9. Installation and Execution

Step 1: Install dependencies.

pip install -r requirements.txt

Step 2: Run the application.

python app.py

Step 3: Open the local application URL displayed in the terminal.

## 10. Testing

The module can be tested using sample phone numbers.

Testing includes:
- Checking a number available in the database.
- Checking a number not available in the database.
- Checking input validation.
- Verifying the displayed results.

## 11. Limitations

- Results depend on the available database records.
- An unreported number is not necessarily safe.
- The current database uses JSON storage.
- The module does not independently verify the identity of callers.

## 12. Future Scope

- Integration with a larger fraud reporting database.
- Real-time suspicious call alerts.
- Improved reporting functionality.
- Enhanced database management.
- Integration with additional FraudX security features.

## 13. Integration

This module is developed as part of the FraudX project.

The call_protection directory contains the backend, frontend, database and documentation required for the module.

## 14. Conclusion

The Call Protection Module aims to improve awareness of suspicious phone numbers by providing a simple database-based checking system.

It contributes to the overall goal of FraudX: helping users identify and understand potential fraud risks.
