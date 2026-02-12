# 🎤 Speaker Speaking Sections API - FINAL GUIDE

New entity `SpeakerSpeakingSection` created with fields:
- `id`
- `title`
- `description`
- `date`
- `speaker` (relationship)

The response objects now include:
- `speakerName`
- `speakerUniversity`
- `speakerImage`
- `speakerType`
- `partnerLogo`
- `linkedin`

---

## **1️⃣ Get ALL Speaking Sections**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections
```

### **Example (cURL):**
```bash
curl -X GET "http://localhost:8080/api/speaker-speaking-sections"
```

---

## **2️⃣ Get Speaking Section by ID**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections/{id}
```

### **Example (cURL):**
```bash
curl -X GET "http://localhost:8080/api/speaker-speaking-sections/1"
```

---

## **3️⃣ Create Speaking Section**

### **Endpoint:**
```
POST http://localhost:8080/api/speaker-speaking-sections/speaker/{speakerId}
```

### **Parameters (Query Params):**
- `title` (String)
- `description` (String)
- `date` (String, e.g., "2024-10-15")
- `username` (String, required for logging)

### **Example (cURL):**
```bash
curl -X POST "http://localhost:8080/api/speaker-speaking-sections/speaker/12?title=Keynote%20Speech&description=AI%20Future&date=2024-10-15&username=admin"
```

---

## **4️⃣ EDIT Speaking Section**

### **Endpoint:**
```
PUT http://localhost:8080/api/speaker-speaking-sections/{id}
```

### **Parameters (Query Params, optional):**
Only send fields you want to update.

- `title` (String)
- `description` (String)
- `date` (String)
- `username` (String, required for logging)

### **Example (cURL):**
```bash
curl -X PUT "http://localhost:8080/api/speaker-speaking-sections/1?title=Updated%20Title&username=admin"
```

---

## **5️⃣ DELETE Speaking Section**

### **Endpoint:**
```
DELETE http://localhost:8080/api/speaker-speaking-sections/{id}
```

### **Parameters (Query Params):**
- `username` (String, required for logging)

### **Example (cURL):**
```bash
curl -X DELETE "http://localhost:8080/api/speaker-speaking-sections/1?username=admin"
```

---

## **6️⃣ Get Sections by Speaker ID**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections/speaker/{speakerId}
```

---

## **7️⃣ Get Sections by Speaker Slug**

### **Endpoint:**
```
GET http://localhost:8080/api/speaker-speaking-sections/speaker/slug/{slug}
```
