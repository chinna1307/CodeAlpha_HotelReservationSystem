# 🏨 Hotel Reservation System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

A simple, user-friendly desktop application for managing hotel room reservations, built entirely with **Java Swing**. This system provides core functionalities for hotel staff to search, book, cancel, and manage guest reservations efficiently. It features a clean GUI and persists all data between sessions.

***

## 📸 Screenshot


*A view of the main application window, showing room search, booking controls, and the reservation list.*

***

## ✨ Key Features

* **🔍 Search Rooms**: Find available rooms by category (`STANDARD`, `DELUXE`, `SUITE`).
* **📝 Book a Room**: Reserve a room for a guest on a specific date with input validation.
* **❌ Cancel a Reservation**: Easily cancel a booking, which frees up the room.
* **💳 Process Payments**: Mark a reservation as paid to track its status.
* **📋 View All Reservations**: See a complete list of all current reservations.
* **💾 Persistent Data**: Room and reservation data is automatically saved to local files (`rooms.dat`, `reservations.dat`), ensuring no data is lost upon closing the app.
* **💬 User Feedback**: A status bar provides real-time feedback on actions performed.
* **🎨 Modern UI**: Uses the Nimbus Look and Feel for a clean, modern aesthetic.

***

## 🛠️ Tech Stack

* **Language**: **Java**
* **GUI Framework**: **Java Swing & AWT**
* **Data Persistence**: **Java Serialization**
* **Date/Time API**: `java.time`

***

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

You need to have a **Java Development Kit (JDK) version 8 or higher** installed on your system.

### Installation & Execution

1.  **Clone the repository (or download the code)**
    ```sh
    git clone [https://github.com/your-username/hotel-reservation-system.git](https://github.com/your-username/hotel-reservation-system.git)
    cd hotel-reservation-system
    ```

2.  **Compile the Java code**
    Open a terminal or command prompt in the project directory and run:
    ```sh
    javac HotelReservationSystem.java
    ```

3.  **Run the application**
    Execute the compiled main class:
    ```sh
    java HotelReservationSystem
    ```
    The application window should now appear! 🎉

***

## 📁 File Structure

When you run the application, it will automatically generate two data files in the root directory:

* `rooms.dat`: Stores the state of all hotel rooms (e.g., availability).
* `reservations.dat`: Stores all active reservation records.

**Note:** Deleting these files will reset the application to its default state with no reservations.

***

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

***

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
