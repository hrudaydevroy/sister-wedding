# Wedding Management & Live Streaming Platform

This workspace contains a static frontend for a wedding website with:

- Static invitation, countdown, story, timeline, gallery, RSVP, and more.
- Admin login placeholder (`admin.html`).
- Fixed floating button to jump to gallery.
- Live stream section for embedding YouTube Live.
- Mobile-friendly, PWA-ready design.

## Local Development

1. **Open a terminal** and navigate to the project folder:
   ```powershell
   cd "d:\akka wedding"
   ```

2. **Start a simple HTTP server** (required to serve files with fetch/audio/video). For example, using Python 3:
   ```powershell
   python -m http.server 8000
   ```
   or install [`live-server`](https://www.npmjs.com/package/live-server) and run:
   ```powershell
   npx live-server .
   ```

3. **Open your browser** at `http://localhost:8000` and you can interact with the site.

> The `admin.html` page is just a placeholder; you will need a backend (Spring Boot) with JWT authentication to make it functional.

### Backend (Spring Boot)

A minimal backend is included in the `backend/` subfolder. To start it:

1. Make sure you have JDK 17+ installed and `mvn` on your `PATH`.
2. From the `backend` directory run:
   ```powershell
   cd backend
   mvn spring-boot:run
   ```
3. The server listens on port `8080` by default. You can customize ports and databases via `application.properties`.

The REST API endpoints under `/api/gallery` and `/api/rsvp` are used by the frontend.

#### CORS and troubleshooting

If the browser console shows errors like:

```
Access to fetch ... has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

it means the backend either is not running on `http://localhost:8080` or CORS is not configured correctly. In this template the configuration now allows requests from the development origins `http://127.0.0.1:5501` and `http://localhost:5501` and will return the proper `Access-Control-Allow-Origin` header when the `Origin` header in the request matches one of those values.

Make sure:

- the backend is started before you open the frontend pages
- you completely stop and restart the Spring Boot process after making any code changes
- your request URLs use the correct hostname/port (`http://localhost:8080`)
- the `uploads/` directory is writable by the backend; it will serve uploaded media files.

You can test the API from the command line to verify CORS headers:

```powershell
# returns header for origin 5501
curl.exe -i -H "Origin: http://127.0.0.1:5501" http://localhost:8080/api/gallery
```

Once the backend is running, the gallery and admin dashboard should be able to communicate correctly.

> **Important:** After editing backend code you must stop the running process and restart it so that changes (CORS settings, security rules, etc.) take effect. If you start a second instance while the first is still running, the new process will fail to bind to port `8080`.

## GitHub

To put the project under version control and push to GitHub:

```powershell
cd "d:\akka wedding"
# initialize repository
git init
# add all files
git add .
# create initial commit
git commit -m "Initial wedding site with gallery, live stream and admin login button"
# add remote (replace URL with your repository)
# git remote add origin https://github.com/youruser/your-repo.git
# push to main branch
# git branch -M main
# git push -u origin main
```

Once the repo is on GitHub you can enable GitHub Pages or deploy the frontend via Netlify/Vercel.

## Deployment

- **Frontend**: simply upload the static files (`index.html`, `style.css`, `script.js`, `photos/`, etc.) to Netlify, GitHub Pages, or any static hosting service.
- **Backend**: this workspace contains only the frontend; the Spring Boot backend and MongoDB/Cloudinary integration should be developed separately and hosted (e.g., on Render).

## Next Steps

- Implement the Spring Boot backend with JWT admin authentication, file upload to Cloudinary, and MongoDB metadata storage.
- Connect the frontend to the backend API for gallery and RSVP operations.
- Replace the YouTube `STREAM_ID` in the `live-stream` section with the actual live stream identifier.

Happy wedding planning! 🎉