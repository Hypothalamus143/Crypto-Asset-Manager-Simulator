
---

# **Crypto Asset Manager Simulator – Capstone Project**

## **📌 Project Overview**

CryptoManager is a Java-based application that allows users to manage and monitor their cryptocurrency portfolio. The system enables users to log in, check market prices, manage balances, buy and sell cryptocurrencies, and calculate their net profit. The project demonstrates proper utilization of **Object-Oriented Programming (OOP)** principles, **exception handling**, **file handling**, and a **Graphical User Interface (GUI)**.

This repository contains:

* Source code for the CryptoManager system
* UML Class Diagram (PDF)
* Project documentation (this README)

---

# **🧩 Features**

### **User Account Management**

* User login with validation
* Balance deposit & withdrawal
* Portfolio tracking

### **Cryptocurrency Management**

* Support for multiple crypto assets (Bitcoin, Ethereum, Solana, etc.)
* Buy and sell operations
* Real-time market checking (mock or simulated values depending on implementation)

### **Net Profit Computation**

* Tracks buy price, current price, and amount per asset
* Calculates user-level or asset-level net profit

### **System Infrastructure**

* GUI for an intuitive and clean user experience
* Exception handling for invalid operations
* File handling for persistent user and portfolio data

---

# **📘 OOP Implementation**

## **Abstraction**

* `Asset` is an **abstract class**, representing common behaviors and data shared by all cryptocurrency types.
* Specific coins (Bitcoin, Ethereum, Solana) extend this base class.

## **Encapsulation**

* Sensitive data (e.g., `balance`, `password`, crypto details) are **private fields** accessed through getters and controlled methods.

## **Inheritance**

* Concrete crypto types inherit from the abstract `Asset` class.
* This encourages code reuse and extends system flexibility.

## **Polymorphism**

* Methods such as `getCurrentPrice()` or overridden valuation behavior use **runtime polymorphism** based on asset type.

---

# **📊 Class Diagram**

The full UML Class Diagram can be found here:

📄 **CryptoManager.drawio.pdf** (located in `/diagrams/`)
This diagram includes:

* Classes
* Attributes
* Methods
* Inheritance hierarchy

---

# **🧱 System Architecture**

## **Main Classes**

### **1. CryptoManager**

Handles the core operations:

* `logIn()`
* `checkMarket()`
* `buy()`, `sell()`
* `deposit()`, `withdraw()`
* `getNetProfit()`

### **2. User**

Contains:

* Username, password, and balance
* Portfolio (List of Assets)
* Validation and net profit computation

### **3. Asset (Abstract)**

Common attributes:

* `name`
* `currentPrice`
* `buyPrice`
* `amount`

Child Classes:

* `Bitcoin`
* `Ethereum`
* `Solana`

---

# **⚠️ Exception Handling**

The program includes custom and standard exception handling for:

* Invalid login attempts
* Insufficient balance
* Invalid buy/sell amounts
* File read/write errors

Exceptions are created, thrown, and caught appropriately to ensure stable execution.

---

# **📁 File Handling**

The system uses file handling for:

* Saving user data
* Loading portfolio information
* Storing transaction logs

This ensures program data persists between sessions.

---

# **🖥️ Graphical User Interface (GUI)**

The GUI ensures:

* Clean layout and user-friendly interactions
* Proper organization of buttons, forms, and menus
* Visual clarity when tracking crypto assets and balances

---

# **🏛️ Design Patterns Used**

### **Factory Pattern**

Used for creating cryptocurrency objects (`Bitcoin`, `Ethereum`, `Solana`) without specifying exact classes in the main logic.

---

# **📅 Submission Details**

* GitHub repository shared with the instructor
* Class diagram included (PDF)
* README contains documentation required by the capstone specification
* Repository will be continuously updated until the final presentation

---

# **👥 Team Members**

* John Prince Alonte

---
