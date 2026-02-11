# 📝 SpeakerSection with Separate Networking Insights Fields

## ✅ Updated SpeakerSection Entity

The SpeakerSection entity now has **separate database columns** for networking insights:

### **All Fields:**
- `id` - Primary key
- `title` - Section title (e.g., "About", "Networking Insights")
- `content` - Main content (TEXT)
- `orderIndex` - Display order
- **`priorities`** - Priorities (TEXT, optional) ⭐ NEW
- **`currentFocus`** - Current focus (TEXT, optional) ⭐ NEW
- **`futureFocus`** - Future focus (TEXT, optional) ⭐ NEW
- `speaker` - Reference to Speaker (ManyToOne)

---

## 📋 Complete JSON Response Example

```json
{
  "id": 1,
  "name": "Prof. Yanda Li",
  "university": "Tsinghua University",
  "slug": "prof-yanda-li",
  "linkedin": "https://www.linkedin.com/in/yanda-li",
  "partnerLogo": "https://example.com/logos/tsinghua.png",
  "sections": [
    {
      "id": 1,
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert in robotics...",
      "orderIndex": 1,
      "priorities": null,
      "currentFocus": null,
      "futureFocus": null
    },
    {
      "id": 2,
      "title": "Networking Insights",
      "content": "General networking information",
      "orderIndex": 2,
      "priorities": "Building collaborative research networks across academia and industry",
      "currentFocus": "AI safety and ethics in autonomous robotic systems",
      "futureFocus": "Next-generation medical robotics and healthcare applications"
    }
  ]
}
```

---

## 🚀 How to Create Sections with Networking Insights

### **Method 1: Bulk Insert (Recommended)**

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}/bulk`

**Request Body:**
```json
[
  {
    "title": "About",
    "content": "Prof. Yanda Li is an internationally recognized expert in robotics and artificial intelligence with over 15 years of research experience.",
    "orderIndex": 1
  },
  {
    "title": "Networking Insights",
    "content": "Prof. Li has built extensive networks in both academic and industry sectors.",
    "orderIndex": 2,
    "priorities": "Building collaborative research networks across academia and industry",
    "currentFocus": "AI safety and ethics in autonomous robotic systems",
    "futureFocus": "Next-generation medical robotics and healthcare applications"
  }
]
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/11/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert...",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "Prof. Li has built extensive networks...",
      "orderIndex": 2,
      "priorities": "Building collaborative research networks",
      "currentFocus": "AI safety and ethics in robotics",
      "futureFocus": "Medical robotics and healthcare"
    }
  ]'
```

---

### **Method 2: Single Section**

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}`

**Example:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/11" \
  -d "title=Networking Insights" \
  -d "content=General networking overview" \
  -d "orderIndex=2" \
  -d "priorities=Building research networks" \
  -d "currentFocus=AI safety in robotics" \
  -d "futureFocus=Healthcare robotics"
```

---

## 🎨 Frontend Usage Examples

### **JavaScript - Display Networking Insights**

```javascript
// Fetch speaker
const response = await fetch('/api/speakers/slug/prof-yanda-li');
const speaker = await response.json();

// Find Networking Insights section
const networkingSection = speaker.sections.find(s => s.title === 'Networking Insights');

if (networkingSection) {
  console.log('Content:', networkingSection.content);
  console.log('Priorities:', networkingSection.priorities);
  console.log('Current Focus:', networkingSection.currentFocus);
  console.log('Future Focus:', networkingSection.futureFocus);
}
```

### **React Component**

```jsx
function NetworkingInsights({ section }) {
  if (!section || section.title !== 'Networking Insights') return null;
  
  return (
    <div className="networking-insights">
      <h2>Networking Insights</h2>
      
      {section.content && (
        <div className="overview">
          <p>{section.content}</p>
        </div>
      )}
      
      {section.priorities && (
        <div className="insight-item">
          <h3>Priorities</h3>
          <p>{section.priorities}</p>
        </div>
      )}
      
      {section.currentFocus && (
        <div className="insight-item">
          <h3>Current Focus</h3>
          <p>{section.currentFocus}</p>
        </div>
      )}
      
      {section.futureFocus && (
        <div className="insight-item">
          <h3>Future Focus</h3>
          <p>{section.futureFocus}</p>
        </div>
      )}
    </div>
  );
}

// Usage
function SpeakerProfile({ speaker }) {
  const networkingSection = speaker.sections.find(s => s.title === 'Networking Insights');
  
  return (
    <div>
      <h1>{speaker.name}</h1>
      
      {/* About section */}
      {speaker.sections.find(s => s.title === 'About') && (
        <div className="about">
          <h2>About</h2>
          <p>{speaker.sections.find(s => s.title === 'About').content}</p>
        </div>
      )}
      
      {/* Networking Insights */}
      <NetworkingInsights section={networkingSection} />
    </div>
  );
}
```

---

## 📊 Database Schema

After restarting the application, JPA will create these new columns in `speaker_sections` table:

```sql
ALTER TABLE speaker_sections 
ADD COLUMN priorities TEXT,
ADD COLUMN current_focus TEXT,
ADD COLUMN future_focus TEXT;
```

**Table Structure:**
```
speaker_sections
├── id (BIGINT, PRIMARY KEY)
├── title (VARCHAR, NOT NULL)
├── content (TEXT)
├── order_index (INT, NOT NULL)
├── priorities (TEXT) ⭐ NEW
├── current_focus (TEXT) ⭐ NEW
├── future_focus (TEXT) ⭐ NEW
└── speaker_id (BIGINT, FOREIGN KEY)
```

---

## 💡 Complete Example: Create Speaker with Networking Insights

### Step 1: Create Speaker
```bash
curl -X POST http://localhost:8080/api/speakers/robotics \
  -F "image=@prof-yanda-li.jpg" \
  -F "name=Prof. Yanda Li" \
  -F "university=Tsinghua University" \
  -F "conferencecode=ROBOTICS2024" \
  -F "speakerType=Keynote Speaker" \
  -F "username=admin"
# Response: {"id": 11, ...}
```

### Step 2: Update with LinkedIn, Slug, Partner Logo
```bash
curl -X PUT "http://localhost:8080/api/speakers/11" \
  -F "slug=prof-yanda-li" \
  -F "linkedin=https://www.linkedin.com/in/yanda-li" \
  -F "partnerLogo=https://example.com/logos/tsinghua.png" \
  -F "username=admin"
```

### Step 3: Add Sections with Networking Insights
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/11/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert in robotics and artificial intelligence with over 15 years of research experience. His pioneering work in autonomous systems has significantly influenced the field.",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "Prof. Li has established a strong global network combining academic excellence with industry collaboration.",
      "orderIndex": 2,
      "priorities": "Building collaborative research networks across leading universities and tech companies",
      "currentFocus": "Advancing AI safety and ethical frameworks for autonomous robotic systems",
      "futureFocus": "Developing next-generation medical robotics for precision healthcare and surgical applications"
    }
  ]'
```

### Step 4: Verify
```bash
curl -X GET "http://localhost:8080/api/speakers/slug/prof-yanda-li"
```

**Expected Response:**
```json
{
  "id": 11,
  "name": "Prof. Yanda Li",
  "university": "Tsinghua University",
  "slug": "prof-yanda-li",
  "linkedin": "https://www.linkedin.com/in/yanda-li",
  "partnerLogo": "https://example.com/logos/tsinghua.png",
  "sections": [
    {
      "id": 25,
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert...",
      "orderIndex": 1,
      "priorities": null,
      "currentFocus": null,
      "futureFocus": null
    },
    {
      "id": 26,
      "title": "Networking Insights",
      "content": "Prof. Li has established a strong global network...",
      "orderIndex": 2,
      "priorities": "Building collaborative research networks...",
      "currentFocus": "Advancing AI safety and ethical frameworks...",
      "futureFocus": "Developing next-generation medical robotics..."
    }
  ]
}
```

---

## ✅ Key Points

✅ **Separate Fields** - priorities, currentFocus, futureFocus are now database columns  
✅ **All Optional** - Can be null, not required  
✅ **TEXT Type** - Supports long content (up to 65,535 characters each)  
✅ **Flexible** - Can be used for any section, not just "Networking Insights"  
✅ **JSON Response** - Automatically included in API responses  

---

## 🔄 Update Existing Section

**Endpoint:** `PUT /api/speaker-sections/{id}`

```bash
curl -X PUT "http://localhost:8080/api/speaker-sections/26" \
  -d "priorities=Updated priorities text" \
  -d "currentFocus=Updated current focus" \
  -d "futureFocus=Updated future focus"
```

---

**All fields are now separate database columns! Restart your application to apply the schema changes.** 🚀
