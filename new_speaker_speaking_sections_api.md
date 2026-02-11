# 🎤 Speaker Speaking Sections API

New entity `SpeakerSpeakingSection` created with fields:
- `id`
- `title`
- `description`
- `date`
- `speaker` (relationship)

---

## **1️⃣ Create Speaking Section**

### **Endpoint:**
```
POST http://localhost:8080/api/speaker-speaking-sections/speaker/{speakerId}
```

### **Parameters (Query Params):**
- `title` (String)
- `description` (String)
- `date` (String, e.g., "2024-10-15")

### **Example (cURL):**
```bash
curl -X POST "http://localhost:8080/api/speaker-speaking-sections/speaker/12?title=Keynote%20Speech&description=AI%20Future&date=2024-10-15"
```

---

## **2️⃣ Get Sections by Speaker ID**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections/speaker/{speakerId}
```

---

## **3️⃣ Get Sections by Speaker Slug**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections/speaker/slug/{slug}
```

### **Example:**
```bash
curl -X GET "http://localhost:8080/api/speaker-speaking-sections/speaker/slug/prof-yanda-li"
```

---

## **4️⃣ Update Speaking Section**

### **Endpoint:**
```
PUT http://localhost:8080/api/speaker-speaking-sections/{id}
```

### **Parameters (Query Params, optional):**
- `title`
- `description`
- `date`

### **Example:**
```bash
curl -X PUT "http://localhost:8080/api/speaker-speaking-sections/1?title=Updated%20Title"
```

---

## **5️⃣ Delete Speaking Section**

### **Endpoint:**
```
DELETE http://localhost:8080/api/speaker-speaking-sections/{id}
```
