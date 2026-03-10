# Quick Start Guide - IN10NT EMS

## Starting the Application

### Method 1: Use the Batch Files (Recommended)
```bash
# Start backend (run this first)
start-backend.bat

# Start frontend (run this in a new terminal)
start-frontend.bat
```

### Method 2: Manual Commands
```bash
# Backend (from root directory)
cd backend
mvn spring-boot:run

# Frontend (from root directory, new terminal)
cd frontend  
npm run dev
```

## Login Credentials
- **Email**: admin@in10nt.com
- **Password**: admin123

## Application URLs
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8000

## Important Notes
- Always start the backend FIRST, then the frontend
- Make sure MySQL is running on localhost:3306
- If you get port conflicts, use the fix-issues.bat script

## Troubleshooting
If login still shows "Invalid credentials":
1. Check that both backend and frontend are running
2. Clear browser cache and cookies
3. Check browser console (F12) for error messages
4. Verify the backend is accessible at http://localhost:8000