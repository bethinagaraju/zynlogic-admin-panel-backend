# 📝 UPDATE SPEAKER API - With Partner Logo Upload

## **API Endpoint**

```
PUT http://localhost:8080/api/speakers/{id}
```

Replace `{id}` with the speaker ID (e.g., `1`, `2`, `11`, etc.)

---

## **Content-Type**

```
multipart/form-data
```

---

## **Request Parameters**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `image` | **File** | ❌ No | Speaker profile image file |
| `imageUrl` | String | ❌ No | Image URL (if not uploading file) |
| `name` | String | ❌ No | Speaker name |
| `university` | String | ❌ No | University/Organization |
| `conferencecode` | String | ❌ No | Conference code |
| `speakerType` | String | ❌ No | Speaker type |
| `visible` | Boolean | ❌ No | Visibility status |
| `slug` | String | ❌ No | URL-friendly identifier |
| `linkedin` | String | ❌ No | LinkedIn profile URL |
| **`partnerLogo`** | **File** | ❌ No | **Partner logo image file** ⭐ UPDATED |
| `username` | String | ✅ **YES** | For audit logging |

---

## **✨ What Changed:**

### **Before:**
```
partnerLogo: "https://example.com/logo.png"  (URL string)
```

### **Now:**
```
partnerLogo: [FILE UPLOAD]  (Image file - uploaded to FTP)
```

**Partner logo now works exactly like the speaker image!**

---

## **📤 Upload Examples**

### **Example 1: Upload Speaker Image + Partner Logo**

**URL:**
```
PUT http://localhost:8080/api/speakers/11
```

**Request Body (form-data):**
```
image: [SELECT FILE] speaker-photo.jpg
partnerLogo: [SELECT FILE] company-logo.png
slug: prof-yanda-li
linkedin: https://www.linkedin.com/in/yanda-li
username: admin
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/api/speakers/11" \
  -F "image=@/path/to/speaker-photo.jpg" \
  -F "partnerLogo=@/path/to/company-logo.png" \
  -F "slug=prof-yanda-li" \
  -F "linkedin=https://www.linkedin.com/in/yanda-li" \
  -F "username=admin"
```

---

### **Example 2: Upload Only Partner Logo**

**URL:**
```
PUT http://localhost:8080/api/speakers/11
```

**Request Body (form-data):**
```
partnerLogo: [SELECT FILE] tsinghua-logo.png
username: admin
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/api/speakers/11" \
  -F "partnerLogo=@/path/to/tsinghua-logo.png" \
  -F "username=admin"
```

---

### **Example 3: Update All Fields with File Uploads**

**cURL:**
```bash
curl -X PUT "http://localhost:8080/api/speakers/11" \
  -F "image=@speaker.jpg" \
  -F "partnerLogo=@logo.png" \
  -F "name=Prof. Yanda Li" \
  -F "university=Tsinghua University" \
  -F "slug=prof-yanda-li" \
  -F "linkedin=https://linkedin.com/in/yanda-li" \
  -F "speakerType=Keynote Speaker" \
  -F "username=admin"
```

---

## **📋 Postman Setup**

1. **Method:** PUT
2. **URL:** `http://localhost:8080/api/speakers/11`
3. **Body:** Select "form-data"
4. **Add fields:**

| Key | Type | Value |
|-----|------|-------|
| partnerLogo | **File** | [Select file: logo.png] |
| image | **File** | [Select file: speaker.jpg] |
| slug | Text | prof-yanda-li |
| linkedin | Text | https://linkedin.com/in/yanda-li |
| username | Text | admin |

**Important:** Change type from "Text" to "File" for `image` and `partnerLogo` fields!

---

## **🎯 Success Response**

**Status:** `200 OK`

```json
{
  "id": 11,
  "name": "Prof. Yanda Li",
  "university": "Tsinghua University",
  "conferencecode": "ROBOTICS2024",
  "imagePath": "https://yourcdn.com/1234567890_speaker.jpg",
  "speakerType": "Keynote Speaker",
  "orderIndex": 1,
  "visible": true,
  "slug": "prof-yanda-li",
  "linkedin": "https://www.linkedin.com/in/yanda-li",
  "partnerLogo": "https://yourcdn.com/logo_1234567890_tsinghua.png",
  "sections": []
}
```

**Note:** 
- `imagePath` - Auto-generated URL after uploading speaker image
- `partnerLogo` - Auto-generated URL after uploading partner logo

---

## **🔧 How It Works**

### **File Upload Process:**

1. **You upload:** `tsinghua-logo.png`
2. **System generates filename:** `logo_1707649200000_tsinghua-logo.png`
3. **Uploads to FTP server**
4. **Returns public URL:** `https://yourcdn.com/logo_1707649200000_tsinghua-logo.png`
5. **Saves URL to database** in `partnerLogo` field

---

## **📝 JavaScript/Fetch Example**

```javascript
async function updateSpeakerWithLogos(speakerId, speakerImage, logoImage, otherData) {
  const formData = new FormData();
  
  // Add file uploads
  if (speakerImage) {
    formData.append('image', speakerImage); // File object
  }
  
  if (logoImage) {
    formData.append('partnerLogo', logoImage); // File object
  }
  
  // Add other fields
  if (otherData.slug) formData.append('slug', otherData.slug);
  if (otherData.linkedin) formData.append('linkedin', otherData.linkedin);
  if (otherData.name) formData.append('name', otherData.name);
  
  // Username is required
  formData.append('username', 'admin');

  const response = await fetch(`http://localhost:8080/api/speakers/${speakerId}`, {
    method: 'PUT',
    body: formData
  });

  return await response.json();
}

// Usage with file input
document.getElementById('uploadButton').addEventListener('click', async () => {
  const speakerImageFile = document.getElementById('speakerImage').files[0];
  const logoFile = document.getElementById('partnerLogo').files[0];
  
  const result = await updateSpeakerWithLogos(11, speakerImageFile, logoFile, {
    slug: 'prof-yanda-li',
    linkedin: 'https://linkedin.com/in/yanda-li'
  });
  
  console.log('Updated:', result);
});
```

---

## **🖼️ React Example**

```jsx
function SpeakerUpdateForm({ speakerId }) {
  const [speakerImage, setSpeakerImage] = useState(null);
  const [partnerLogo, setPartnerLogo] = useState(null);
  const [linkedin, setLinkedin] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const formData = new FormData();
    if (speakerImage) formData.append('image', speakerImage);
    if (partnerLogo) formData.append('partnerLogo', partnerLogo);
    if (linkedin) formData.append('linkedin', linkedin);
    formData.append('username', 'admin');

    const response = await fetch(`/api/speakers/${speakerId}`, {
      method: 'PUT',
      body: formData
    });

    const result = await response.json();
    console.log('Updated:', result);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Speaker Image:</label>
        <input 
          type="file" 
          accept="image/*"
          onChange={(e) => setSpeakerImage(e.target.files[0])} 
        />
      </div>
      
      <div>
        <label>Partner Logo:</label>
        <input 
          type="file" 
          accept="image/*"
          onChange={(e) => setPartnerLogo(e.target.files[0])} 
        />
      </div>
      
      <div>
        <label>LinkedIn:</label>
        <input 
          type="text" 
          value={linkedin}
          onChange={(e) => setLinkedin(e.target.value)} 
        />
      </div>
      
      <button type="submit">Update Speaker</button>
    </form>
  );
}
```

---

## **✅ Summary**

| Field | Old Behavior | New Behavior |
|-------|-------------|--------------|
| `image` | File upload → FTP | File upload → FTP ✅ |
| `partnerLogo` | ~~String URL~~ | **File upload → FTP** ⭐ NEW |
| `linkedin` | String URL | String URL ✅ |
| `slug` | String | String ✅ |

**Now both `image` and `partnerLogo` work the same way - upload files and get auto-generated URLs!** 🚀

---

## **🔥 Quick Test**

```bash
curl -X PUT "http://localhost:8080/api/speakers/11" \
  -F "partnerLogo=@/path/to/your-logo.png" \
  -F "username=admin"
```

The uploaded logo will be:
1. Uploaded to your FTP server
2. Given a unique filename (e.g., `logo_1707649200000_your-logo.png`)
3. Public URL saved to database
4. Returned in the response

**Perfect!** 🎉
