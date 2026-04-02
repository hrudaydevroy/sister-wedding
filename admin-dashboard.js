const BACKEND = "https://digital-wedding-invitation-1.onrender.com";

const imagesTab = document.getElementById("imagesTab");
const videosTab = document.getElementById("videosTab");
const rsvpTab = document.getElementById("rsvpTab");

const imagesSection = document.getElementById("imagesSection");
const videosSection = document.getElementById("videosSection");
const rsvpSection = document.getElementById("rsvpSection");

function activate(tab, section) {

  const tabs = [imagesTab, videosTab, rsvpTab];
  const sections = [imagesSection, videosSection, rsvpSection];

  tabs.forEach(t => {
    if (t) t.classList.remove("active");
  });

  sections.forEach(s => {
    if (s) s.classList.remove("active");
  });

  if (tab) tab.classList.add("active");
  if (section) section.classList.add("active");
}

imagesTab.onclick = () => {
  activate(imagesTab, imagesSection);
  loadImages();
};

videosTab.onclick = () => {
  activate(videosTab, videosSection);
  loadVideos();
};

rsvpTab.onclick = () => {
  activate(rsvpTab, rsvpSection);
  loadRSVPs();
};

/* UPLOAD */

async function upload(files, type) {

  const data = new FormData();

  for (let f of files) {
    data.append("file", f);
  }

  data.append("type", type);

  await fetch(BACKEND + "/api/gallery/upload", {
    method: "POST",
    body: data
  });

}

/* IMAGE UPLOAD */

document.getElementById("imageForm").onsubmit = async (e) => {

  e.preventDefault();

  const files = e.target.file.files;

  await upload(files, "image");

  loadImages();

};

/* VIDEO UPLOAD */

document.getElementById("videoForm").onsubmit = async (e) => {

  e.preventDefault();

  const files = e.target.file.files;

  await upload(files, "video");

  loadVideos();

};

/* LOAD IMAGES */

async function loadImages() {

  const res = await fetch(BACKEND + "/api/gallery");

  const items = await res.json();

  const images = items.filter(i => i.type === "image");

  render(images, "imageList");

}

/* LOAD VIDEOS */

async function loadVideos() {

  const res = await fetch(BACKEND + "/api/gallery");

  const items = await res.json();

  const videos = items.filter(i => i.type === "video");

  render(videos, "videoList");

}

/* RENDER */

function render(items, container) {

  let html = "<table><tr><th>Preview</th><th>Type</th><th>Action</th></tr>";

  items.forEach(i => {

    html += `
<tr>

<td class="preview">
${i.type === "image"
      ? `<img src="${BACKEND + i.mediaUrl}" width="80">`
      : `<video src="${BACKEND + i.mediaUrl}" width="150" controls></video>`}
</td>

<td>${i.type}</td>

<td>
<button onclick="deleteItem('${i.id}')" class="delete">Delete</button>

<a href="${BACKEND + i.mediaUrl}" download>
<button class="download">Download</button>
</a>

</td>

</tr>
`;

  });

  html += "</table>";

  document.getElementById(container).innerHTML = html;

}

/* DELETE */

async function deleteItem(id) {

  await fetch(BACKEND + "/api/gallery/" + id, {
    method: "DELETE"
  });

  loadImages();
  loadVideos();

}

/* RSVP */

async function loadRSVPs() {

  const res = await fetch(BACKEND + "/api/rsvp");

  const data = await res.json();

  const tbody = document.querySelector("#rsvpTable tbody");

  tbody.innerHTML = "";

  data.forEach(r => {

    const tr = document.createElement("tr");

    tr.innerHTML = `
<td>${r.name}</td>
<td>${r.email}</td>
<td>${r.guests}</td>
<td>${r.message || ""}</td>
<td>${r.createdAt || ""}</td>
`;

    tbody.appendChild(tr);

  });

}

loadImages();