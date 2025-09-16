import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

// Room, Reservation, and Hotel classes remain unchanged from the previous version
class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    enum Category { STANDARD, DELUXE, SUITE }
    private int roomNumber;
    private Category category;
    private boolean isAvailable;

    public Room(int roomNumber, Category category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public Category getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - " + (isAvailable ? "Available" : "Booked");
    }
}

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guestName;
    private Room room;
    private LocalDate date;
    private boolean paid;

    public Reservation(String guestName, Room room, LocalDate date) {
        this.guestName = guestName;
        this.room = room;
        this.date = date;
        this.paid = false;
    }

    public String getGuestName() { return guestName; }
    public Room getRoom() { return room; }
    public LocalDate getDate() { return date; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override
    public String toString() {
        return guestName + " - " + room + " on " + date + " - " + (paid ? "Paid" : "Unpaid");
    }
}

class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;
    private static final String ROOMS_FILE = "rooms.dat";
    private static final String RESERVATIONS_FILE = "reservations.dat";

    public Hotel() {
        rooms = loadRooms();
        reservations = loadReservations();
    }

    @SuppressWarnings("unchecked")
    private List<Room> loadRooms() {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) {
            return initializeDefaultRooms();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Room>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
            return initializeDefaultRooms();
        }
    }

    private List<Room> initializeDefaultRooms() {
        List<Room> defaultRooms = new ArrayList<>();
        for (int i = 1; i <= 5; i++) defaultRooms.add(new Room(i, Room.Category.STANDARD));
        for (int i = 6; i <= 8; i++) defaultRooms.add(new Room(i, Room.Category.DELUXE));
        for (int i = 9; i <= 10; i++) defaultRooms.add(new Room(i, Room.Category.SUITE));
        saveRooms(defaultRooms);
        return defaultRooms;
    }

    private void saveRooms(List<Room> rooms) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Reservation> loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Reservation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    public List<Room> searchRooms(Room.Category category) {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCategory() == category && room.isAvailable()) {
                available.add(room);
            }
        }
        return available;
    }

    public boolean bookRoom(String guestName, int roomNumber, LocalDate date) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber && room.isAvailable()) {
                room.setAvailable(false);
                Reservation res = new Reservation(guestName, room, date);
                reservations.add(res);
                saveRooms(rooms);
                saveReservations();
                return true;
            }
        }
        return false;
    }

    public boolean cancelReservation(String guestName, int roomNumber) {
        Iterator<Reservation> it = reservations.iterator();
        while (it.hasNext()) {
            Reservation res = it.next();
            if (res.getGuestName().equals(guestName) && res.getRoom().getRoomNumber() == roomNumber) {
                res.getRoom().setAvailable(true);
                it.remove();
                saveRooms(rooms);
                saveReservations();
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean payReservation(String guestName, int roomNumber) {
        for (Reservation res : reservations) {
            if (res.getGuestName().equals(guestName) && res.getRoom().getRoomNumber() == roomNumber) {
                res.setPaid(true);
                saveReservations();
                return true;
            }
        }
        return false;
    }
}

public class HotelReservationSystem extends JFrame {
    private Hotel hotel;
    private JComboBox<Room.Category> categoryBox;
    private JTextArea roomArea, reservationArea;
    private JTextField nameField, roomField, dateField;
    private JButton searchBtn, bookBtn, cancelBtn, payBtn, viewBtn;
    private JLabel statusLabel;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public HotelReservationSystem() {
        hotel = new Hotel();
        setTitle("Hotel Reservation System");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        // Initialize components with modern styling
        initComponents();

        // Set up the main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Top panel for search
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for room display
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for booking and reservations
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Status bar at the bottom
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(new Color(0, 128, 0)); // Green for status

        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // Apply Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error setting look and feel: " + ex.getMessage());
            }
        }
    }

    private void initComponents() {
        // Initialize components with tooltips and fonts
        categoryBox = new JComboBox<>(Room.Category.values());
        categoryBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryBox.setToolTipText("Select room category");

        searchBtn = new JButton("Search Rooms");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setToolTipText("Search for available rooms");

        roomArea = new JTextArea(10, 50);
        roomArea.setFont(new Font("Arial", Font.PLAIN, 14));
        roomArea.setEditable(false);
        roomArea.setToolTipText("Available rooms in selected category");

        reservationArea = new JTextArea(5, 50);
        reservationArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reservationArea.setEditable(false);
        reservationArea.setToolTipText("Current reservations");

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setToolTipText("Enter guest name");

        roomField = new JTextField(5);
        roomField.setFont(new Font("Arial", Font.PLAIN, 14));
        roomField.setToolTipText("Enter room number");

        dateField = new JTextField(10);
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setToolTipText("Enter date (YYYY-MM-DD)");

        bookBtn = new JButton("Book Room");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setToolTipText("Book the selected room");

        cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(220, 20, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setToolTipText("Cancel an existing reservation");

        payBtn = new JButton("Pay");
        payBtn.setFont(new Font("Arial", Font.BOLD, 14));
        payBtn.setBackground(new Color(255, 165, 0));
        payBtn.setForeground(Color.WHITE);
        payBtn.setToolTipText("Mark reservation as paid");

        viewBtn = new JButton("View Reservations");
        viewBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewBtn.setBackground(new Color(70, 130, 180));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setToolTipText("View all reservations");

        // Add action listeners
        searchBtn.addActionListener(e -> searchRooms());
        bookBtn.addActionListener(e -> bookRoom());
        cancelBtn.addActionListener(e -> cancelReservation());
        payBtn.addActionListener(e -> payReservation());
        viewBtn.addActionListener(e -> viewReservations());
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(new TitledBorder("Search Rooms"));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryBox);
        topPanel.add(searchBtn);
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new TitledBorder("Available Rooms"));
        JScrollPane roomScroll = new JScrollPane(roomArea);
        centerPanel.add(roomScroll, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Booking panel
        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bookingPanel.setBorder(new TitledBorder("Book Room"));
        bookingPanel.add(new JLabel("Name:"));
        bookingPanel.add(nameField);
        bookingPanel.add(new JLabel("Room #:"));
        bookingPanel.add(roomField);
        bookingPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        bookingPanel.add(dateField);
        bookingPanel.add(bookBtn);
        bookingPanel.add(cancelBtn);
        bookingPanel.add(payBtn);

        // Reservation panel
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.setBorder(new TitledBorder("Reservations"));
        reservationPanel.add(viewBtn, BorderLayout.NORTH);
        JScrollPane resScroll = new JScrollPane(reservationArea);
        reservationPanel.add(resScroll, BorderLayout.CENTER);

        // Add to bottom panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(bookingPanel, gbc);
        gbc.gridy = 1;
        bottomPanel.add(reservationPanel, gbc);

        return bottomPanel;
    }

    private void searchRooms() {
        Room.Category cat = (Room.Category) categoryBox.getSelectedItem();
        List<Room> available = hotel.searchRooms(cat);
        roomArea.setText("");
        for (Room room : available) {
            roomArea.append(room.toString() + "\n");
        }
        if (available.isEmpty()) {
            roomArea.setText("No available rooms in this category.");
            statusLabel.setText("No rooms available.");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Rooms loaded successfully.");
            statusLabel.setForeground(new Color(0, 128, 0));
        }
    }

    private void bookRoom() {
        String name = nameField.getText().trim();
        String roomNumStr = roomField.getText().trim();
        String dateStr = dateField.getText().trim();

        if (name.isEmpty() || roomNumStr.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields (Name, Room #, Date).");
            statusLabel.setText("Missing fields.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            int roomNum = Integer.parseInt(roomNumStr);
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm booking for " + name + " in Room " + roomNum + " on " + date + "?", 
                "Confirm Booking", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = hotel.bookRoom(name, roomNum, date);
                JOptionPane.showMessageDialog(this, success ? "Room booked!" : "Booking failed.");
                statusLabel.setText(success ? "Booking successful." : "Booking failed.");
                statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
                searchRooms();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid room number.");
            statusLabel.setText("Invalid room number.");
            statusLabel.setForeground(Color.RED);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            statusLabel.setText("Invalid date format.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void cancelReservation() {
        String name = nameField.getText().trim();
        String roomNumStr = roomField.getText().trim();

        if (name.isEmpty() || roomNumStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name and Room #.");
            statusLabel.setText("Missing fields.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            int roomNum = Integer.parseInt(roomNumStr);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Cancel reservation for " + name + " in Room " + roomNum + "?", 
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = hotel.cancelReservation(name, roomNum);
                JOptionPane.showMessageDialog(this, success ? "Reservation cancelled." : "Cancel failed.");
                statusLabel.setText(success ? "Cancellation successful." : "Cancellation failed.");
                statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
                searchRooms();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid room number.");
            statusLabel.setText("Invalid room number.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void payReservation() {
        String name = nameField.getText().trim();
        String roomNumStr = roomField.getText().trim();

        if (name.isEmpty() || roomNumStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name and Room #.");
            statusLabel.setText("Missing fields.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            int roomNum = Integer.parseInt(roomNumStr);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Mark payment for " + name + " in Room " + roomNum + "?", 
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = hotel.payReservation(name, roomNum);
                JOptionPane.showMessageDialog(this, success ? "Payment successful." : "Payment failed.");
                statusLabel.setText(success ? "Payment successful." : "Payment failed.");
                statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
                viewReservations();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid room number.");
            statusLabel.setText("Invalid room number.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void viewReservations() {
        reservationArea.setText("");
        List<Reservation> reservations = hotel.getReservations();
        for (Reservation res : reservations) {
            reservationArea.append(res.toString() + "\n");
        }
        if (reservations.isEmpty()) {
            reservationArea.setText("No reservations.");
            statusLabel.setText("No reservations found.");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Reservations loaded successfully.");
            statusLabel.setForeground(new Color(0, 128, 0));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelReservationSystem().setVisible(true));
    }
}