# 📝 Speaker Entity - Complete Field Reference

## Updated Speaker Entity Fields

The Speaker entity now includes the following fields:

### Basic Information
- `id` - Primary key (auto-generated)
- `name` - Speaker name
- `university` - University or organization
- `conferencecode` - Conference code
- `speakerType` - Type of speaker (e.g., "Keynote", "Guest")
- `orderIndex` - Display order
- `visible` - Visibility status (boolean)

### Additional Fields
- `imagePath` - Speaker profile image URL
- `slug` - URL-friendly identifier (unique)
- **`linkedin`** - LinkedIn profile URL ⭐ NEW
- **`partnerLogo`** - Partner logo image path/URL ⭐ NEW

### Relationships
- `sections` - List of SpeakerSection (OneToMany, cascade ALL, orphan removal)

---

## 📋 Complete API Response Example

```json
{
  "id": 1,
  "name": "Prof. Yanda Li",
  "university": "Tsinghua University",
  "conferencecode": "ROBOTICS2024",
  "imagePath": "https://example.com/speakers/prof-yanda-li.jpg",
  "speakerType": "Keynote Speaker",
  "orderIndex": 1,
  "visible": true,
  "slug": "prof-yanda-li",
  "linkedin": "https://www.linkedin.com/in/yanda-li",
  "partnerLogo": "https://example.com/logos/tsinghua-logo.png",
  "sections": [
    {
      "id": 1,
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert in robotics and artificial intelligence...",
      "orderIndex": 1
    },
    {
      "id": 2,
      "title": "Networking Insights",
      "content": "{\"priorities\": \"Building collaborative research networks\", \"current-focus\": \"AI safety and ethics in robotics\", \"future-focus\": \"Autonomous systems for healthcare\"}",
      "orderIndex": 2
    }
  ]
}
```

---

## 🔧 How to Update Speaker with New Fields

### Update LinkedIn and Partner Logo

**Endpoint:** `PUT /api/speakers/{id}`

**Example cURL:**
```bash
curl -X PUT "http://localhost:8080/api/speakers/1" \
  -F "linkedin=https://www.linkedin.com/in/yanda-li" \
  -F "partnerLogo=https://example.com/logos/tsinghua-logo.png" \
  -F "username=admin"
```

### Complete Update with All Fields

```bash
curl -X PUT "http://localhost:8080/api/speakers/1" \
  -F "name=Prof. Yanda Li" \
  -F "slug=prof-yanda-li" \
  -F "linkedin=https://www.linkedin.com/in/yanda-li" \
  -F "partnerLogo=https://example.com/logos/tsinghua-logo.png" \
  -F "university=Tsinghua University" \
  -F "speakerType=Keynote Speaker" \
  -F "username=admin"
```

---

## 📝 How to Add Networking Insights with Structured Data

Since "Networking Insights" has nested fields (priorities, current-focus, future-focus), 
store it as **JSON** in the content field of SpeakerSection.

### Option 1: Store as JSON String

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}`

```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1" \
  -d "title=Networking Insights" \
  -d 'content={"priorities": "Building collaborative networks", "current-focus": "AI safety in robotics", "future-focus": "Autonomous healthcare systems"}' \
  -d "orderIndex=2"
```

### Option 2: Bulk Insert with JSON Content

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}/bulk`

```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "Prof. Yanda Li is an internationally recognized expert...",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "{\"priorities\": \"Building collaborative research networks\", \"current-focus\": \"AI safety and ethics in robotics\", \"future-focus\": \"Autonomous systems for healthcare\"}",
      "orderIndex": 2
    }
  ]'
```

---

## 🎨 Frontend Usage

### JavaScript - Parse Networking Insights JSON

```javascript
// Fetch speaker
const response = await fetch('/api/speakers/slug/prof-yanda-li');
const speaker = await response.json();

// Find Networking Insights section
const networkingSection = speaker.sections.find(s => s.title === 'Networking Insights');

if (networkingSection) {
  // Parse JSON content
  const insights = JSON.parse(networkingSection.content);
  
  console.log('Priorities:', insights.priorities);
  console.log('Current Focus:', insights['current-focus']);
  console.log('Future Focus:', insights['future-focus']);
}

// Display LinkedIn
if (speaker.linkedin) {
  console.log('LinkedIn:', speaker.linkedin);
}

// Display Partner Logo
if (speaker.partnerLogo) {
  console.log('Partner Logo:', speaker.partnerLogo);
}
```

### React Component Example

```jsx
function SpeakerProfile({ speaker }) {
  // Parse Networking Insights
  const networkingSection = speaker.sections.find(s => s.title === 'Networking Insights');
  const insights = networkingSection ? JSON.parse(networkingSection.content) : null;
  
  return (
    <div className="speaker-profile">
      <img src={speaker.imagePath} alt={speaker.name} />
      <h1>{speaker.name}</h1>
      <p>{speaker.university}</p>
      
      {/* LinkedIn */}
      {speaker.linkedin && (
        <a href={speaker.linkedin} target="_blank" rel="noopener">
          <i className="fab fa-linkedin"></i> LinkedIn
        </a>
      )}
      
      {/* Partner Logo */}
      {speaker.partnerLogo && (
        <img src={speaker.partnerLogo} alt="Partner Logo" className="partner-logo" />
      )}
      
      {/* About Section */}
      {speaker.sections.find(s => s.title === 'About') && (
        <div className="about-section">
          <h2>About</h2>
          <p>{speaker.sections.find(s => s.title === 'About').content}</p>
        </div>
      )}
      
      {/* Networking Insights */}
      {insights && (
        <div className="networking-insights">
          <h2>Networking Insights</h2>
          <div>
            <h3>Priorities</h3>
            <p>{insights.priorities}</p>
          </div>
          <div>
            <h3>Current Focus</h3>
            <p>{insights['current-focus']}</p>
          </div>
          <div>
            <h3>Future Focus</h3>
            <p>{insights['future-focus']}</p>
          </div>
        </div>
      )}
    </div>
  );
}
```

---

## 💡 Complete Workflow Example

### Step 1: Create Speaker
```bash
curl -X POST http://localhost:8080/api/speakers/robotics \
  -F "image=@speaker.jpg" \
  -F "name=Prof. Yanda Li" \
  -F "university=Tsinghua University" \
  -F "conferencecode=ROBOTICS2024" \
  -F "speakerType=Keynote Speaker" \
  -F "username=admin"
```

### Step 2: Update with Slug, LinkedIn, Partner Logo
```bash
curl -X PUT "http://localhost:8080/api/speakers/1" \
  -F "slug=prof-yanda-li" \
  -F "linkedin=https://www.linkedin.com/in/yanda-li" \
  -F "partnerLogo=https://example.com/logos/tsinghua.png" \
  -F "username=admin"
```

### Step 3: Add Sections (About + Networking Insights)
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "Prof. Yanda Li is a leading researcher in robotics and AI with over 15 years of experience.",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "{\"priorities\": \"Building global research collaborations\", \"current-focus\": \"AI ethics and safety in autonomous systems\", \"future-focus\": \"Next-generation medical robotics\"}",
      "orderIndex": 2
    }
  ]'
```

### Step 4: Verify
```bash
curl -X GET "http://localhost:8080/api/speakers/slug/prof-yanda-li"
```

---

## 📊 Database Schema Changes

After restarting the application, JPA will create these new columns:

**speakers table:**
```sql
ALTER TABLE speakers ADD COLUMN linkedin VARCHAR(255);
ALTER TABLE speakers ADD COLUMN partner_logo VARCHAR(255);
```

---

## ✅ Summary of New Fields

| Field | Type | Purpose | Example |
|-------|------|---------|---------|
| `linkedin` | String | LinkedIn profile URL | `https://www.linkedin.com/in/yanda-li` |
| `partnerLogo` | String | Partner logo image path | `https://example.com/logos/tsinghua.png` |
| Networking Insights | JSON in section content | Structured networking data | See JSON example above |

---

## 🔧 Update Endpoints

### Update LinkedIn Only
```bash
PUT /api/speakers/{id}?linkedin=https://linkedin.com/in/profile&username=admin
```

### Update Partner Logo Only
```bash
PUT /api/speakers/{id}?partnerLogo=https://example.com/logo.png&username=admin
```

### Update Both
```bash
PUT /api/speakers/{id}?linkedin=URL&partnerLogo=URL&username=admin
```

---

**All new fields are now integrated! Restart your application to apply the changes.** 🚀
