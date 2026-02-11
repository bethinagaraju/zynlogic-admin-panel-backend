# 📚 Updated SpeakerSection API Documentation

## ✅ **Fields Removed:**
- ❌ **title** - No longer exists
- ❌ **orderIndex** - No longer exists

## **Current SpeakerSection Fields:**
- `id` - Primary key (auto-generated)
- `content` - Main content (TEXT)
- `priorities` - Networking priorities (TEXT, optional)
- `currentFocus` - Current focus (TEXT, optional)
- `futureFocus` - Future focus (TEXT, optional)
- `speaker` - Reference to Speaker (ManyToOne)

---

## **📋 Complete API List**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/speaker-sections/speaker/{speakerId}` | Create single section |
| POST | `/api/speaker-sections/speaker/{speakerId}/bulk` | Create multiple sections |
| GET | `/api/speaker-sections` | Get all sections |
| GET | `/api/speaker-sections/{id}` | Get section by ID |
| GET | `/api/speaker-sections/speaker/{speakerId}` | Get sections by speaker ID |
| PUT | `/api/speaker-sections/{id}` | Update section |
| DELETE | `/api/speaker-sections/{id}` | Delete section |

---

## **1️⃣ CREATE SINGLE SECTION**

### **Endpoint:**
```
POST /api/speaker-sections/speaker/{speakerId}
```

### **Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `content` | String | ✅ Yes | Main content |
| `priorities` | String | ❌ No | Networking priorities |
| `currentFocus` | String | ❌ No | Current focus |
| `futureFocus` | String | ❌ No | Future focus |

### **Example:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1" \
  -d "content=Prof. Yanda Li is an expert in robotics..."
```

### **With Networking Insights:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1" \
  -d "content=General overview of networking activities" \
  -d "priorities=Building collaborative research networks" \
  -d "currentFocus=AI safety and ethics" \
  -d "futureFocus=Medical robotics"
```

### **Response:** `201 Created`
```json
{
  "id": 1,
  "content": "General overview of networking activities",
  "priorities": "Building collaborative research networks",
  "currentFocus": "AI safety and ethics",
  "futureFocus": "Medical robotics"
}
```

---

## **2️⃣ CREATE MULTIPLE SECTIONS (BULK)**

### **Endpoint:**
```
POST /api/speaker-sections/speaker/{speakerId}/bulk
```

### **Content-Type:** `application/json`

### **Request Body:**
```json
[
  {
    "content": "string (required)",
    "priorities": "string (optional)",
    "currentFocus": "string (optional)",
    "futureFocus": "string (optional)"
  }
]
```

### **Example:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "content": "Prof. Yanda Li is an internationally recognized expert in robotics and AI."
    },
    {
      "content": "Overview of networking activities",
      "priorities": "Building research networks",
      "currentFocus": "AI safety in robotics",
      "futureFocus": "Healthcare robotics"
    }
  ]'
```

### **Response:** `201 Created`
```json
[
  {
    "id": 1,
    "content": "Prof. Yanda Li is an expert...",
    "priorities": null,
    "currentFocus": null,
    "futureFocus": null
  },
  {
    "id": 2,
    "content": "Overview of networking activities",
    "priorities": "Building research networks",
    "currentFocus": "AI safety in robotics",
    "futureFocus": "Healthcare robotics"
  }
]
```

---

## **3️⃣ GET ALL SECTIONS**

### **Endpoint:**
```
GET /api/speaker-sections
```

### **Example:**
```bash
curl -X GET "http://localhost:8080/api/speaker-sections"
```

### **Response:** `200 OK`
```json
[
  {
    "id": 1,
    "content": "Content...",
    "priorities": null,
    "currentFocus": null,
    "futureFocus": null
  },
  {
    "id": 2,
    "content": "Content...",
    "priorities": "Building networks",
    "currentFocus": "AI safety",
    "futureFocus": "Healthcare"
  }
]
```

---

## **4️⃣ GET SECTION BY ID**

### **Endpoint:**
```
GET /api/speaker-sections/{id}
```

### **Example:**
```bash
curl -X GET "http://localhost:8080/api/speaker-sections/1"
```

### **Response:** `200 OK`
```json
{
  "id": 1,
  "content": "Prof. Yanda Li is an expert...",
  "priorities": "Building networks",
  "currentFocus": "AI safety",
  "futureFocus": "Healthcare robotics"
}
```

---

## **5️⃣ GET SECTIONS BY SPEAKER ID** ⭐

### **Endpoint:**
```
GET /api/speaker-sections/speaker/{speakerId}
```

### **Example:**
```bash
curl -X GET "http://localhost:8080/api/speaker-sections/speaker/1"
```

### **Response:** `200 OK`
```json
[
  {
    "id": 1,
    "content": "About text...",
    "priorities": null,
    "currentFocus": null,
    "futureFocus": null
  },
  {
    "id": 2,
    "content": "Networking overview",
    "priorities": "Building networks",
    "currentFocus": "AI safety",
    "futureFocus": "Healthcare"
  }
]
```

---

## **6️⃣ UPDATE SECTION**

### **Endpoint:**
```
PUT /api/speaker-sections/{id}
```

### **Parameters (ALL OPTIONAL):**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `content` | String | ❌ No | Main content |
| `priorities` | String | ❌ No | Networking priorities |
| `currentFocus` | String | ❌ No | Current focus |
| `futureFocus` | String | ❌ No | Future focus |

### **Example - Update All Fields:**
```bash
curl -X PUT "http://localhost:8080/api/speaker-sections/1" \
  -d "content=Updated content" \
  -d "priorities=Updated priorities" \
  -d "currentFocus=Updated current focus" \
  -d "futureFocus=Updated future focus"
```

### **Example - Update Only One Field:**
```bash
curl -X PUT "http://localhost:8080/api/speaker-sections/1" \
  -d "content=New content text"
```

### **Response:** `200 OK`
```json
{
  "id": 1,
  "content": "Updated content",
  "priorities": "Updated priorities",
  "currentFocus": "Updated current focus",
  "futureFocus": "Updated future focus"
}
```

---

## **7️⃣ DELETE SECTION**

### **Endpoint:**
```
DELETE /api/speaker-sections/{id}
```

### **Example:**
```bash
curl -X DELETE "http://localhost:8080/api/speaker-sections/1"
```

### **Response:** `204 No Content`

---

## **📊 JSON Response Format**

```json
{
  "id": 1,
  "content": "Main content text here",
  "priorities": "Networking priorities text",
  "currentFocus": "Current focus text",
  "futureFocus": "Future focus text"
}
```

**Note:** `title` and `orderIndex` fields have been removed!

---

## **🔧 Complete Workflow Example**

```bash
# 1. Create speaker (if not exists)
curl -X POST http://localhost:8080/api/speakers/robotics \
  -F "image=@speaker.jpg" \
  -F "name=Prof. Yanda Li" \
  -F "university=Tsinghua" \
  -F "conferencecode=ROBOTICS2024" \
  -F "speakerType=Keynote" \
  -F "username=admin"

# 2. Add sections in bulk
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "content": "Prof. Yanda Li is a researcher..."
    },
    {
      "content": "Networking overview",
      "priorities": "Building networks",
      "currentFocus": "AI safety",
      "futureFocus": "Healthcare"
    }
  ]'

# 3. Get all sections for speaker
curl -X GET "http://localhost:8080/api/speaker-sections/speaker/1"

# 4. Update a section
curl -X PUT "http://localhost:8080/api/speaker-sections/2" \
  -d "priorities=Updated priorities text"

# 5. Delete a section (if needed)
curl -X DELETE "http://localhost:8080/api/speaker-sections/2"
```

---

## **⚠️ Important Changes**

### **What Was Removed:**
- ❌ `title` field - No longer in database
- ❌ `orderIndex` field - No longer in database
- ❌ Sections are no longer ordered automatically

### **Database Migration:**
After restarting your application, JPA will:
```sql
ALTER TABLE speaker_sections DROP COLUMN title;
ALTER TABLE speaker_sections DROP COLUMN order_index;
```

---

**Restart your application to apply these changes!** 🚀
