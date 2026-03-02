/* ================================
   GOOGLE MAP NAVIGATION
================================ */

function navigate() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(position => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;

      const destination = encodeURIComponent(
        "8WW6+4MV, Srikakulam, Gudem, Andhra Pradesh 532484, India"
      );

      window.open(
        `https://www.google.com/maps/dir/${lat},${lng}/${destination}`,
        "_blank"
      );
    });
  } else {
    alert("Geolocation is not supported by this browser.");
  }
}
/* ================================
   DYNAMIC COUNTDOWN
================================ */

const weddingDate = new Date("April 11, 2026 00:15:00").getTime();

function updateCountdown() {
  const now = new Date().getTime();
  const distance = weddingDate - now;

  if (distance <= 0) {
    document.querySelector(".countdown").innerHTML =
      "<h2>💍 Today is the Wedding Day 💍<br>Welcome to our Forever ❤️</h2>";
    return;
  }

  const days = Math.floor(distance / (1000 * 60 * 60 * 24));
  const hours = Math.floor((distance / (1000 * 60 * 60)) % 24);
  const minutes = Math.floor((distance / (1000 * 60)) % 60);
  const seconds = Math.floor((distance / 1000) % 60);

  document.getElementById("days").innerText = days;
  document.getElementById("hours").innerText = hours;
  document.getElementById("minutes").innerText = minutes;
  document.getElementById("seconds").innerText = seconds;
}

setInterval(updateCountdown, 1000);
updateCountdown();

/* ================================
   QR AUTO UPDATE AFTER DEPLOY
================================ */

window.addEventListener("load", () => {
  const currentURL = window.location.href;
  const qrImage = document.getElementById("qrImage");

  qrImage.src =
    "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" +
    encodeURIComponent(currentURL);
});

/* ================================
   STORY SCROLL ANIMATION
================================ */

document.addEventListener("DOMContentLoaded", () => {
  const storyCards = document.querySelectorAll(".story-card");

  const storyObserver = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add("show");
          storyObserver.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.2 }
  );

  storyCards.forEach(card => storyObserver.observe(card));
});

/* ================================
   LIGHTBOX
================================ */

function openLightbox(img) {
  document.getElementById("lightbox").style.display = "flex";
  document.getElementById("lightbox-img").src = img.src;
}

function closeLightbox() {
  document.getElementById("lightbox").style.display = "none";
}

/* ================================
   PRE-WEDDING SLIDER
================================ */

const pwTrack = document.querySelector(".pw-slider-track");
let pwCards = document.querySelectorAll(".pw-card");

let pwIndex = Math.floor(pwCards.length / 2);

function pwUpdate() {
  pwCards.forEach(card => card.classList.remove("active"));
  pwCards[pwIndex].classList.add("active");

  const cardWidth = pwCards[0].offsetWidth + 30;
  const centerOffset =
    pwIndex * cardWidth -
    pwTrack.parentElement.offsetWidth / 2 +
    cardWidth / 2;

  pwTrack.style.transform = `translateX(${-centerOffset}px)`;
}

function pwSlideLeft() {
  if (pwIndex > 0) {
    pwIndex--;
    pwUpdate();
  }
}

function pwSlideRight() {
  if (pwIndex < pwCards.length - 1) {
    pwIndex++;
    pwUpdate();
  }
}

window.addEventListener("resize", pwUpdate);
pwUpdate();

/* ================================
   NAVBAR SCROLL EFFECT
================================ */

window.addEventListener("scroll", () => {
  const navbar = document.querySelector(".navbar");
  if (window.scrollY > 80) {
    navbar.classList.add("scrolled");
  } else {
    navbar.classList.remove("scrolled");
  }
});

/* ================================
   BACKEND ORIGIN (used for API requests)
================================ */
const BACKEND_ORIGIN = 'http://localhost:8080';

/* ================================
   RSVP
================================ */

const rsvpForm = document.getElementById("rsvpForm");
if (rsvpForm) {
  rsvpForm.addEventListener("submit", async e => {
    e.preventDefault();
    const formData = new FormData(rsvpForm);
    const data = {
      name: formData.get('name'),
      email: formData.get('email'),
      guests: parseInt(formData.get('number')) || 0,
      message: formData.get('message')
    };
    try {
      const resp = await fetch(`${BACKEND_ORIGIN}/api/rsvp`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      if (resp.ok) {
        const result = await resp.json();
        alert(`💖 Thank you ${result.name} for confirming${result.message ? ' ("' + result.message + '")' : ''}! See you at the wedding!`);
        rsvpForm.reset();
        // open admin dashboard so host can review RSVPs
        window.open('admin-dashboard.html', '_blank');
      } else {
        alert('Something went wrong submitting RSVP.');
      }
    } catch (err) {
      console.error('RSVP error', err);
      alert('Unable to submit RSVP. Is the backend running?');
    }
  });
}

/* ================================
   DYNAMIC GALLERY FETCH
================================ */

// base address for backend API – ensures requests work even when page is opened via file://
// (already declared earlier, keep for backward compatibility)
// const BACKEND_ORIGIN = 'http://localhost:8080';

async function loadGalleryItems() {
  try {
    const res = await fetch(`${BACKEND_ORIGIN}/api/gallery`);
    if (!res.ok) return;
    const items = await res.json();
    const track = document.querySelector('.pw-slider-track');
    if (!track) return;
    track.innerHTML = '';
    items.forEach(item => {
      const card = document.createElement('div');
      card.className = 'pw-card';
      const img = document.createElement('img');
      // mediaUrl starts with '/uploads/...' so prefix backend origin
      img.src = BACKEND_ORIGIN + item.mediaUrl;
      img.onclick = () => openLightbox(img);
      card.appendChild(img);
      track.appendChild(card);
    });
    // recalc cards
    pwCards = document.querySelectorAll('.pw-card');
    pwIndex = Math.floor(pwCards.length / 2);
    pwUpdate();
  } catch (err) {
    console.error('Could not load gallery', err);
  }
}

window.addEventListener('load', () => {
  // hide gallery video tag if file not found
  const vid = document.querySelector('#gallery video');
  if (vid) {
    vid.addEventListener('error', () => {
      vid.style.display = 'none';
    });
  }
  // handle live stream load failure
  const liveFrame = document.querySelector('#live iframe');
  if (liveFrame) {
    liveFrame.addEventListener('error', () => {
      liveFrame.parentElement.innerHTML = '<p>Live stream unavailable.</p>';
    });
  }
  loadGalleryItems();
});

/* ================================
   DARK MODE
================================ */

document.getElementById("themeToggle").addEventListener("click", () => {
  document.body.classList.toggle("dark");
});

/* ================================
   WHATSAPP SHARE
================================ */

function shareWhatsApp() {
  const text = 
`💍 Wedding Invitation 💍
📅 11 April 2026
📍 8WW7+55H, Gudem, Andhra Pradesh 532484

Join us to celebrate ❤️
👉 ${window.location.href}`;

  window.open(
    `https://wa.me/?text=${encodeURIComponent(text)}`,
    "_blank"
  );
}

/* ================================
   PHOTO PREVIEW
================================ */

/* fixed gallery button behavior; open viewer page */
const galleryBtn = document.getElementById("galleryBtn");
if (galleryBtn) {
  galleryBtn.addEventListener("click", () => {
    window.location.href = 'viewer.html';
  });
}

/* ================================
   VOICE RECORD
================================ */

let recorder, audioChunks = [];

async function startRecording(){
  const stream = await navigator.mediaDevices.getUserMedia({ audio:true });
  recorder = new MediaRecorder(stream);
  recorder.start();

  recorder.ondataavailable = e => audioChunks.push(e.data);
}

function stopRecording(){
  recorder.stop();
  recorder.onstop = () => {
    const audioBlob = new Blob(audioChunks);
    const audioUrl = URL.createObjectURL(audioBlob);
    document.getElementById("voicePlayback").src = audioUrl;
    audioChunks = [];
  };
}
/* ================================
   TRACK SECTION SCROLL ANIMATION
================================ */

document.addEventListener("DOMContentLoaded", () => {

  const timelineCards = document.querySelectorAll(".timeline .card");

  const timelineObserver = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add("show");
        }
      });
    },
    { threshold: 0.3 }
  );

  timelineCards.forEach(card => {
    timelineObserver.observe(card);
  });

});