# SmartPDFChatBot

SmartPDFChatBot is a Java-based NLP chatbot that allows users to upload a PDF file, ask context-based questions, and receive accurate answers and summaries from the content. It integrates AI-driven natural language processing, document handling, and a persistent database for efficient knowledge retrieval.

---

## 📌 Features

* 📄 Upload and process PDF documents
* 💬 Ask context-aware questions about uploaded content
* 🧠 NLP-powered answer generation
* 📝 Extractive summarization in custom points
* 💾 Persistent MySQL database for question-answer history
* 🚫 Robust exception handling for various error scenarios

---

## ⚙️ Technology Stack

* **Java** - Core application logic
* **Apache PDFBox** - PDF parsing
* **OpenAI GPT / Gemini API** - NLP and QnA services
* **MySQL** - Persistent storage for questions, answers, and document history
* **JDBC** - Database connectivity

---

## 🧩 Modules

### 1. **PDF Processing**

* Validates and extracts text from uploaded PDFs
* Handles exceptions such as:

  * `InvalidPDFException`: Thrown if file is corrupted, unreadable, or encrypted
  * `EmptyPDFException`: Thrown if the file has no readable content

---

### 2. **QnA Module**

* Extracts relevant answers using NLP
* Stores and retrieves Q\&A from the database
* Integrates with Gemini or GPT APIs
* Handles:

  * `AnswerNotFoundException`: Triggered when no relevant answer is found

---

### 3. **Summarization**

* Summarizes content using a frequency-based scoring system
* Allows customization of number of summary points

---

### 4. **Database Service (MySQL)**

* Persists PDF content
* Stores and retrieves question-answer pairs
* Maintains user query history
* Provides functionalities to view and delete history
* Optimized schema for fast lookup and indexing

---

### 5. **Exception Handling**

* Custom exception classes:

  * `InvalidPDFException`
  * `EmptyPDFException`
  * `AnswerNotFoundException`
* Ensures program continues gracefully after encountering errors

---

## 🚀 Getting Started

### Requirements

* Java 11+
* Maven
* MySQL 8+

### Setup Steps

```bash
1. Clone the repository
2. Import the project into your IDE (IntelliJ recommended)
3. Create a MySQL database named `smartpdf`
4. Run the schema script from `/database/schema.sql`
5. Configure DB credentials in `DBService.java`
6. Build and run `ChatBotApp.java` (located in root of project)
```

---

## 📂 Project Structure

```
SmartPDFChatBot/
├── ChatBotApp.java
├── exceptions/
│   ├── AnswerNotFoundException.java
│   ├── EmptyPDFException.java
│   └── InvalidPDFException.java
├── lib/
│   ├── mysql-connector-j-8.3.0.jar
│   └── pdfbox-app-2.0.27.jar
├── models/
│   └── PDFDocument.java
├── services/
│   ├── ChatService.java
│   ├── DBService.java
│   ├── PDFProcessor.java
│   ├── PDFService.java
│   └── UserInteractionService.java
└── SmartPDFChatBot.iml
```

---

## 🧪 Sample Use Cases

* Law students finding case details from large legal PDFs
* Medical professionals quickly summarizing reports
* Journalists extracting summaries from research PDFs

---

## 🧑‍💻 Contributors

* **Vedant Singh Thakur**
* **Yashwith Behara**

---

## 🏗️ Future Enhancements

* User authentication & roles
* PDF highlighting & answer referencing
* Multi-language support
* GenAI Integration

---

## 📃 License

MIT License

---

> ⚠️ This is a project developed for educational and research purposes. Please use responsibly and verify generated outputs for critical use cases.
