# 📝 How to Insert Data into SpeakerSection

## Table of Contents
1. [Using REST API (Recommended)](#method-1-using-rest-api)
2. [Using Cascade from Speaker](#method-2-using-cascade-from-speaker)
3. [Direct Database Insert](#method-3-direct-database-insert)

---

## Method 1: Using REST API (Recommended) ✅

### A. Create a Single Section

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}`

**Request Parameters:**
- `title` (string, required) - Section title (e.g., "About", "Experience")
- `content` (string, required) - Section content (can be long text)
- `orderIndex` (integer, required) - Display order (1, 2, 3...)

**Example cURL:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1" \
  -d "title=About" \
  -d "content=John is a leading expert in robotics with over 20 years of experience in autonomous systems and AI." \
  -d "orderIndex=1"
```

**Example using Postman:**
- Method: POST
- URL: `http://localhost:8080/api/speaker-sections/speaker/1`
- Body (form-data):
  - title: `About`
  - content: `John is a leading expert...`
  - orderIndex: `1`

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "About",
  "content": "John is a leading expert in robotics with over 20 years of experience in autonomous systems and AI.",
  "orderIndex": 1
}
```

---

### B. Create Multiple Sections at Once (Bulk Insert)

**Endpoint:** `POST /api/speaker-sections/speaker/{speakerId}/bulk`

**Content-Type:** `application/json`

**Request Body:**
```json
[
  {
    "title": "About",
    "content": "John is a leading expert in robotics with over 20 years of experience...",
    "orderIndex": 1
  },
  {
    "title": "Networking Insights",
    "content": "With a vast network spanning academia and industry, John has collaborated with...",
    "orderIndex": 2
  },
  {
    "title": "Experience",
    "content": "Previously worked at Boston Dynamics, NASA, and MIT...",
    "orderIndex": 3
  },
  {
    "title": "Research Focus",
    "content": "Current research focuses on swarm robotics and machine learning...",
    "orderIndex": 4
  }
]
```

**Example cURL:**
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "John is a leading expert...",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "With a vast network...",
      "orderIndex": 2
    }
  ]'
```

**Response:** `201 Created`
```json
[
  {
    "id": 1,
    "title": "About",
    "content": "John is a leading expert...",
    "orderIndex": 1
  },
  {
    "id": 2,
    "title": "Networking Insights",
    "content": "With a vast network...",
    "orderIndex": 2
  }
]
```

---

### C. JavaScript/Fetch Example (Frontend)

**Single Section:**
```javascript
async function addSpeakerSection(speakerId, title, content, orderIndex) {
  const formData = new URLSearchParams();
  formData.append('title', title);
  formData.append('content', content);
  formData.append('orderIndex', orderIndex);

  const response = await fetch(`/api/speaker-sections/speaker/${speakerId}`, {
    method: 'POST',
    body: formData
  });

  const section = await response.json();
  console.log('Section created:', section);
  return section;
}

// Usage
addSpeakerSection(1, 'About', 'John is a leading expert...', 1);
```

**Bulk Sections:**
```javascript
async function addMultipleSections(speakerId, sections) {
  const response = await fetch(`/api/speaker-sections/speaker/${speakerId}/bulk`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(sections)
  });

  const createdSections = await response.json();
  console.log('Sections created:', createdSections);
  return createdSections;
}

// Usage
const sections = [
  { title: 'About', content: 'Bio text...', orderIndex: 1 },
  { title: 'Experience', content: 'Work history...', orderIndex: 2 }
];
addMultipleSections(1, sections);
```

---

## Method 2: Using Cascade from Speaker

Since we configured `cascade = CascadeType.ALL` and `orphanRemoval = true`, you can also add sections by updating the speaker entity.

### Option A: Add to Existing Speaker Service

Add this method to `SpeakerService.java`:

```java
@Transactional
public Speaker addSectionsToSpeaker(Long speakerId, List<SpeakerSectionDTO> sectionsData) {
    Speaker speaker = speakerRepository.findById(speakerId)
            .orElseThrow(() -> new IllegalArgumentException("Speaker not found"));
    
    for (SpeakerSectionDTO dto : sectionsData) {
        SpeakerSection section = new SpeakerSection(
            dto.getTitle(), 
            dto.getContent(), 
            dto.getOrderIndex()
        );
        section.setSpeaker(speaker);
        speaker.getSections().add(section);
    }
    
    return speakerRepository.save(speaker); // Cascades to sections
}
```

This approach automatically saves sections when you save the speaker.

---

## Method 3: Direct Database Insert

If you need to insert data directly (e.g., for initial setup or migration):

### SQL Script:

```sql
-- First, make sure you have a speaker (check existing speakers)
SELECT * FROM speakers;

-- Insert sections for speaker with id = 1
INSERT INTO speaker_sections (speaker_id, title, content, order_index) VALUES
(1, 'About', 'John is a leading expert in robotics with over 20 years of experience in autonomous systems and AI. His work has influenced the development of next-generation robots used in healthcare, manufacturing, and space exploration.', 1),

(1, 'Networking Insights', 'With a vast network spanning academia and industry, John has collaborated with researchers from MIT, Stanford, and leading tech companies. He regularly mentors young engineers and speaks at international conferences.', 2),

(1, 'Experience', 'Previously worked at Boston Dynamics as a Senior Robotics Engineer, NASA Jet Propulsion Laboratory on Mars Rover projects, and served as a Professor at MIT for 10 years. Currently leads the Robotics Research Lab at Harvard University.', 3),

(1, 'Research Focus', 'Current research focuses on swarm robotics, human-robot interaction, and applying machine learning to autonomous decision-making. Published over 150 peer-reviewed papers and holds 23 patents in robotics technology.', 4),

(1, 'Awards & Recognition', 'Recipient of the IEEE Robotics and Automation Award (2020), NSF CAREER Award (2015), and named one of MIT Technology Review''s 35 Innovators Under 35 (2012).', 5);

-- Verify the insert
SELECT * FROM speaker_sections WHERE speaker_id = 1 ORDER BY order_index;
```

---

## 📊 Complete API Reference

### SpeakerSection Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/speaker-sections/speaker/{speakerId}` | Create single section |
| POST | `/api/speaker-sections/speaker/{speakerId}/bulk` | Create multiple sections |
| GET | `/api/speaker-sections` | Get all sections |
| GET | `/api/speaker-sections/{id}` | Get section by ID |
| PUT | `/api/speaker-sections/{id}` | Update section |
| DELETE | `/api/speaker-sections/{id}` | Delete section |

---

## 🎯 Recommended Workflow

### Step 1: Create a Speaker with Slug
```bash
# First create speaker (already have this)
curl -X POST http://localhost:8080/api/speakers/robotics \
  -F "image=@speaker.jpg" \
  -F "name=John Doe" \
  -F "university=MIT" \
  -F "conferencecode=ROBOTICS2024" \
  -F "speakerType=Keynote" \
  -F "username=admin"
```

### Step 2: Update Speaker to Add Slug
```bash
# Update speaker to add slug
curl -X PUT http://localhost:8080/api/speakers/1 \
  -F "slug=john-doe" \
  -F "username=admin"
```

### Step 3: Add Sections (Bulk)
```bash
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/1/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "John is a leading expert in robotics...",
      "orderIndex": 1
    },
    {
      "title": "Networking Insights",
      "content": "With a vast network...",
      "orderIndex": 2
    },
    {
      "title": "Experience",
      "content": "Previously worked at Boston Dynamics...",
      "orderIndex": 3
    }
  ]'
```

### Step 4: Verify
```bash
# Fetch speaker with sections
curl -X GET http://localhost:8080/api/speakers/slug/john-doe
```

---

## 🔧 Update & Delete Operations

### Update a Section
```bash
curl -X PUT "http://localhost:8080/api/speaker-sections/1" \
  -d "title=About (Updated)" \
  -d "content=Updated bio content..." \
  -d "orderIndex=1"
```

### Delete a Section
```bash
curl -X DELETE http://localhost:8080/api/speaker-sections/1
```

---

## 💡 Pro Tips

1. **Use Bulk Insert**: More efficient for creating multiple sections
2. **OrderIndex**: Start from 1 and increment for proper ordering
3. **Content Length**: TEXT field supports long content (up to 65,535 characters)
4. **Cascade Delete**: When you delete a speaker, sections are auto-deleted
5. **Validation**: Add `@Valid` and validation annotations for production use

---

## ✅ Complete Example: Create Speaker with Sections

```bash
# 1. Create speaker
SPEAKER_ID=$(curl -s -X POST http://localhost:8080/api/speakers/robotics \
  -F "image=@speaker.jpg" \
  -F "name=Dr. Sarah Johnson" \
  -F "university=Stanford University" \
  -F "conferencecode=ROBOTICS2024" \
  -F "speakerType=Keynote Speaker" \
  -F "username=admin" | jq -r '.id')

# 2. Add slug
curl -X PUT "http://localhost:8080/api/speakers/$SPEAKER_ID" \
  -F "slug=dr-sarah-johnson" \
  -F "username=admin"

# 3. Add sections
curl -X POST "http://localhost:8080/api/speaker-sections/speaker/$SPEAKER_ID/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "title": "About",
      "content": "Dr. Sarah Johnson is a renowned roboticist...",
      "orderIndex": 1
    },
    {
      "title": "Research Areas",
      "content": "Specializes in soft robotics and bio-inspired design...",
      "orderIndex": 2
    }
  ]'

# 4. Verify
curl -X GET http://localhost:8080/api/speakers/slug/dr-sarah-johnson
```

---

## 🎨 Frontend Form Example (HTML)

```html
<form id="addSectionForm">
  <input type="hidden" id="speakerId" value="1">
  
  <label>Title:</label>
  <input type="text" id="title" required>
  
  <label>Content:</label>
  <textarea id="content" rows="10" required></textarea>
  
  <label>Order:</label>
  <input type="number" id="orderIndex" min="1" required>
  
  <button type="submit">Add Section</button>
</form>

<script>
document.getElementById('addSectionForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const speakerId = document.getElementById('speakerId').value;
  const formData = new URLSearchParams({
    title: document.getElementById('title').value,
    content: document.getElementById('content').value,
    orderIndex: document.getElementById('orderIndex').value
  });
  
  const response = await fetch(`/api/speaker-sections/speaker/${speakerId}`, {
    method: 'POST',
    body: formData
  });
  
  if (response.ok) {
    alert('Section added successfully!');
    e.target.reset();
  }
});
</script>
```

---

**You now have complete APIs to manage speaker sections! Use the bulk endpoint for efficiency.** 🚀
