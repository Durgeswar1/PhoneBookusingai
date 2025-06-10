# PhoneBookusingai
A console-based Java application to manage contacts with multiple phone numbers, supporting features like adding, updating, deleting, searching, sorting, grouping by country code, and exporting to CSV. Includes data persistence using serialization.
# PhoneBook - Java Contact Management Application

This is a simple console-based Java application to manage a phone book with contacts. Each contact can have multiple phone numbers with country codes and an email address.

## Features
- Add new contacts with validation for email and phone number format by country code
- Display contacts sorted by name
- Search contacts by partial name match
- Update contact details including phone numbers and email
- Delete contacts by name or phone number
- Group and display contacts by phone number country code
- Export contacts to a CSV file (`contacts.csv`)
- Count total contacts
- Persistent storage of contacts using Java serialization (`contacts.dat`)

## Supported Country Codes & Number Lengths
- USA (+1): 10 digits
- UK (+44): 10 digits
- India (+91): 10 digits
- Japan (+81): 10 digits
- Australia (+61): 9 digits

*(You can easily add more country codes and number lengths in the code)*

## Usage
Run the `PhoneBook` class. It presents a menu for interacting with your contact list.

```bash
javac PhoneBook.java
java PhoneBook
Contacts are saved automatically on exit and loaded on startup from contacts.dat.
