# DSA Analyzer — Frontend

React frontend for the DSA Problem Analyzer platform.

## Quick Start

```bash
# 1. Install dependencies
npm install

# 2. Set up environment
cp .env.example .env
# Edit .env → set REACT_APP_API_URL to your Spring Boot backend URL

# 3. Start dev server
npm start
# Opens at http://localhost:3000
```

## Project Structure

```
src/
├── api/
│   └── api.js              # Axios instance + all endpoint functions
├── context/
│   └── AuthContext.jsx     # Global auth state (JWT + user)
├── components/
│   ├── Navbar.jsx          # Top navigation bar
│   ├── Navbar.css
│   └── ProtectedRoute.jsx  # Redirect-if-not-logged-in wrapper
├── pages/
│   ├── Login.jsx           # /login
│   ├── Register.jsx        # /register
│   ├── Auth.css            # Shared auth page styles
│   ├── ProblemList.jsx     # /problems — browse & filter
│   ├── ProblemList.css
│   ├── ProblemDetail.jsx   # /problems/:id — Monaco editor + analysis
│   ├── ProblemDetail.css
│   ├── SubmissionHistory.jsx  # /history — user's past submissions
│   └── SubmissionHistory.css
├── styles/
│   └── globals.css         # Design tokens, resets, utility classes
├── App.jsx                 # Router + AuthProvider root
└── index.js                # React DOM entry point
```

## Pages & Routes

| Route           | Page              | Auth Required |
|-----------------|-------------------|---------------|
| `/`             | → `/problems`     | No            |
| `/problems`     | Problem list      | No            |
| `/problems/:id` | Problem detail    | No (analyze needs login) |
| `/login`        | Login             | No            |
| `/register`     | Register          | No            |
| `/history`      | Submission history | **Yes**      |

## API Integration

All calls go through `src/api/api.js`. The Axios instance:
- Reads `REACT_APP_API_URL` from `.env` (defaults to `http://localhost:8080/api`)
- Automatically attaches `Authorization: Bearer <token>` to every request
- Redirects to `/login` on any 401 response

## Expected Backend Response Shape

Your Spring Boot API should return:
```json
{ "success": true, "data": <payload>, "message": "..." }
```

The frontend reads `res.data.data` for all payloads.

### Login response `data` field:
```json
{ "token": "<jwt>", "username": "alice" }
```

### Analysis response `data` field:
```json
{
  "timeComplexity": "O(n)",
  "spaceComplexity": "O(1)",
  "isOptimal": false,
  "feedback": "Consider using a hash map to reduce lookup time..."
}
```

## Build for Production

```bash
npm run build
# Output in /build — deploy to Vercel, Netlify, etc.
```

For Vercel: just push to GitHub and connect the repo. Set `REACT_APP_API_URL` as an environment variable in Vercel's dashboard pointing to your Railway/Render backend URL.
