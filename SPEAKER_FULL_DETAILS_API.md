# 🎤 Speaker Full Details API

Get ALL details related to a speaker in a single request:
1. **Speaker Details** (name, bio, images, etc.)
2. **Speaker Sections** (content, priorities, focus)
3. **Speaker Speaking Sections** (titles, descriptions, dates)

---

## **Endpoint**

```
GET http://localhost:8080/api/speakers/slug/{slug}/full
```

### **Example URL**
```
http://localhost:8080/api/speakers/slug/prof-yanda-li/full
```

### **Response Structure**

```json
{
  "speaker": {
    "id": 12,
    "name": "Prof. Yanda Li",
    "university": "Tsinghua University",
    "slug": "prof-yanda-li",
    "imagePath": "...",
    "partnerLogo": "...",
    "sections": [
        {
            "id": 5,
            "content": "...",
            "priorities": "..."
        }
    ]
  },
  "speakingSections": [
    {
        "id": 1,
        "title": "Keynote Speech",
        "description": "Future of AI",
        "date": "2024-10-15"
    },
    {
        "id": 2,
        "title": "Panel Discussion",
        "date": "2024-10-16"
    }
  ]
}
```

---

## **⚠️ Server Must Be Restarted**

Run:
```bash
./mvnw spring-boot:run
```
