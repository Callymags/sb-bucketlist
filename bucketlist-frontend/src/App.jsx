import React from 'react';
import './App.css';
import Products from './components/experiences/ExperienceCard.jsx';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './components/home/Home';
import Navbar from './components/shared/Navbar';
import Footer from './components/shared/Footer';
import { Toaster } from 'react-hot-toast';
import LogIn from './components/auth/LogIn';
import Register from './components/auth/Register';
import ExperienceDetail from './components/experiences/ExperienceDetail';
import Profile from "./components/profile/Profile.jsx";
import BucketListDetail from "./components/bucketlist/BucketListDetail.jsx";
import EditExperience from "./components/experiences/EditExperience";
import CreateExperience from "./components/experiences/CreateExperience.jsx";
import ExperiencesPage from "./components/experiences/ExperiencesPage.jsx";
import ErrorPage from "./components/shared/ErrorPage.jsx";

function App() {
  return (
      <div className="min-h-screen flex flex-col">
        <Router>
          <Navbar />

          <main className="flex-grow">
            <Routes>
              <Route path='/' element={<Home />} />
              <Route path="/experiences/:id" element={<ExperienceDetail />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/bucketlist/details/:id" element={<BucketListDetail />} />
              <Route path="/experiences/edit/:id" element={<EditExperience />} />
              <Route path="/experiences/create" element={<CreateExperience />} />
              <Route path="/experiences" element={<ExperiencesPage />} />
              <Route path="/login" element={<LogIn />} />
              <Route path="/register" element={<Register />} />
              <Route path="*" element={<ErrorPage />} />
            </Routes>
          </main>

          <Footer />
        </Router>

        <Toaster position='bottom-center' />
      </div>
  );
}

export default App;
