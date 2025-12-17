# Crypto Asset Manager Simulator – Capstone Project

## 📌 Project Overview
CryptoManager is a Java-based cryptocurrency portfolio management application that allows users to simulate buying, selling, and tracking various cryptocurrencies. The system features user authentication, real-time market simulation, portfolio analysis, and persistent data storage. The project demonstrates comprehensive utilization of **Object-Oriented Programming (OOP)** principles, **exception handling**, **file handling**, and a **Graphical User Interface (GUI)**.

This repository contains:
- Complete source code for the CryptoManager system
- Comprehensive UML Class Diagram (generated from Mermaid code)
- Project documentation (this README)

---

## 🧩 Features

### User Account Management
- Secure user registration and login with validation
- Balance deposit and withdrawal functionality
- Portfolio tracking with profit/loss calculations

### Cryptocurrency Management
- Support for multiple crypto assets (Bitcoin, Ethereum, Solana)
- Buy and sell operations with real-time price updates
- Market simulation with random price fluctuations

### Portfolio Analytics
- Realized and unrealized profit tracking
- Multiple sorting options (by symbol, value, profit, etc.)
- Net worth calculation across all assets

### System Infrastructure
- Clean GUI with intuitive navigation
- Comprehensive exception handling
- Persistent data storage for users and assets

---

## 📘 OOP Implementation

### Abstraction
- `Validatable` interface for validation logic where Validatable interface classes are required to validate and throw a DialogException for the GUI Error Handling

### Encapsulation
- Private fields with controlled access through getters/setters
- Data hiding in all domain classes (User, Asset, etc.)
- Protected internal states in singleton managers

### Inheritance
- `AssetMetadata` inheritance chain (BitcoinMetadata, EthereumMetadata, SolanaMetadata)
- Exception hierarchy with `DialogException` as base class
- Comparator implementations extending base comparator logic

### Polymorphism
- AssetMetadata subclasses (BitcoinMetadata, EthereumMetadata, SolanaMetadata) are treated as AssetMetadata in the AssetRegistry
- Multiple `Comparator` inner classes for different sorting strategies are treated simply as Comparator
- Subclasses of `DialogException` are caught simply as DialogException
- Runtime method resolution in asset value calculations
- Interface-based validation through `Validatable`

---

## 📊 Class Diagram
The system architecture is built around a comprehensive class structure:

### Core Domain Classes
- **User**: Manages user data, balance, and asset portfolio
- **Asset**: Represents cryptocurrency holdings with buy price and amount
- **AssetMetadata**: Contains cryptocurrency metadata and price history

### Manager Classes (Singleton Pattern)
- **AuthManager**: Handles user authentication and registration
- **MarketManager**: Manages cryptocurrency price updates and market data
- **AssetRegistry**: Registry for all available cryptocurrencies
- **UserRepository**: Handles user data persistence
- **CryptoManager**: Main application controller
- **CryptoManagerGUI**: Graphical user interface controller

### Builder Patterns
- **AssetMetadataBuilder**: Builder pattern for constructing asset metadata


### Factory Patterns
- **AssetFactory**: Factory pattern for creating Asset objects
  **InvalidInputException**: Factory pattern for custom InvalidInputException

### Comparator Classes
- **SymbolComparator**, **TotalValueComparator**, **ProfitAmountComparator**, etc.
- Multiple sorting strategies for portfolio organization

### Exception Hierarchy
- **DialogException**: Base exception with user-friendly messages
- **AccountRegistrationException**: Registration-specific errors
- **InvalidInputException**: Input validation errors

---

## 🏗️ System Architecture

### Main Classes

#### 1. CryptoManager (Singleton)
Core application controller that coordinates all operations:
- `start()` - Main application entry point
- `buyCrypto()`, `sellCrypto()` - Trading operations
- `sortLots()` - Portfolio organization

#### 2. User
Contains user data and portfolio management:
- Username, balance, realized profit
- Portfolio (List of Assets)
- Methods for adding/removing assets and calculating net profit

#### 3. Asset
Represents cryptocurrency holdings:
- Asset metadata reference
- Buy price and amount
- Methods for calculating value and profit

#### 4. AssetMetadata (Abstract Base Class)
Cryptocurrency information container:
- Symbol, name, description, category
- Price data and history
- Methods for price updates and history tracking

#### 5. CryptoManagerGUI (Singleton)
Graphical interface controller:
- Panel management using CardLayout
- Form validation and user input handling
- Real-time portfolio display

---

## ⚠️ Exception Handling
The program implements a comprehensive exception handling system:

### Custom Exception Hierarchy
- **DialogException**: Base class with dialog title and suggestion
- **AccountRegistrationException**: For registration issues (duplicate username, weak password, etc.)
- **InvalidInputException**: For form validation errors (empty fields, invalid formats, etc.)

### Exception Scenarios
- Invalid login attempts
- Insufficient balance for transactions
- Invalid input formats in forms
- File read/write errors
- Duplicate username during registration

All exceptions are created, thrown, and caught appropriately with user-friendly error messages.

---

## 📁 File Handling
The system implements persistent data storage through:

### User Data Persistence
- Serialized user objects stored in files
- Password-protected user accounts
- Portfolio data saved between sessions

### Asset Registry Storage
- CSV-based storage for cryptocurrency metadata
- Price history persistence
- Configurable data directory structure

### File Operations
- Save/load user data on login/logout
- Automatic backup of user information

---

## 🖥️ Graphical User Interface (GUI)
The Swing-based GUI provides:

### Interface Components
- Landing panel with login/registration options
- Portfolio dashboard with asset overview
- Buy/sell transaction forms
- Deposit/withdrawal dialogs

### User Experience Features
- Clean, organized layout using CardLayout
- Real-time balance and portfolio updates
- Confirmation dialogs for critical operations
- Sorting controls for portfolio organization

### Visual Feedback
- Success/error message dialogs
- Graph for price history

---

## 🏛️ Design Patterns Used

### Singleton Pattern
Used for: `AssetRegistry`, `AssetFactory`, `MarketManager`, `AuthManager`, `UserRepository`, `CryptoManager`, `CryptoManagerGUI`

### Flyweight Pattern
**Implementation:** `AssetMetadata` objects (intrinsic state) shared across all `Asset` instances via `AssetRegistry`
**Benefit:** Memory efficiency and consistency - only one metadata instance per cryptocurrency type

### Bridge Pattern
**Implementation:** Separates `Asset` from `AssetMetadata`
**Benefit:** Encapsulation and delegation of metadata responsibilities

### Builder Pattern
Used for: `AssetMetadataBuilder` constructs complex asset metadata objects

### Factory Pattern
Used for: `AssetFactory` creates `Asset` instances using shared metadata

### Strategy Pattern
Used for: Multiple `Comparator` implementations for different sorting algorithms

### DAO Pattern
Used for: `UserRepository` separates data persistence logic

### Template Method Pattern
->
Used for: Validation logic in `Validatable` implementations
---

---

## 🔧 Extensibility
The architecture supports easy addition of:
- New cryptocurrency types
- Additional sorting algorithms
- Different authentication methods
- Alternative storage backends
- Enhanced reporting features
- Additional GUI components

---

## 📝 Notes for Evaluators
1. All OOP principles are demonstrated with practical, working examples
2. Exception handling includes both built-in and comprehensive custom exceptions
3. File handling supports both read and write operations with error recovery
4. GUI includes comprehensive form validation and user feedback mechanisms
5. Design patterns are properly documented, justified, and implemented
6. Code follows clean coding principles with appropriate comments

---

## 👤 Developer
- **John Prince Alonte** - Sole Developer & Architect

---

## 📄 License
Educational Use Only - Capstone Project Submission

---
*This project was developed as a capstone project demonstrating comprehensive understanding of Object-Oriented Programming principles in Java.*