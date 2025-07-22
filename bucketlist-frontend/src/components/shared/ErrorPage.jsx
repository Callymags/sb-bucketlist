import React, { useEffect } from "react";
import { FaExclamationTriangle } from "react-icons/fa";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import AOS from "aos";
import "aos/dist/aos.css";

const ErrorPage = ({
                       message,
                       redirectTo,
                       buttonText
                   }) => {
    const { user } = useSelector(state => state.auth);

    useEffect(() => {
        AOS.init({ duration: 1500, once: true });
    }, []);

    // Fallback logic if props are not explicitly passed
    const fallbackRedirect = user ? "/profile" : "/";
    const fallbackButtonText = user ? "Go to Profile" : "Return to Home";

    return (
        <div className="min-h-[calc(100vh-64px)] flex justify-center items-center px-4 py-10">
            <div
                data-aos="fade-up"
                className="text-center shadow-custom px-6 py-10 rounded-md w-full max-w-md"
            >
                <FaExclamationTriangle className="text-6xl text-orange-500 mb-6 mx-auto" />

                <h1 className="text-slate-800 text-2xl sm:text-3xl font-bold mb-4">
                    Oops! Something went wrong.
                </h1>

                <p className="text-gray-600 mb-6">
                    {message ??
                        "The page you are looking for doesn't exist or an unexpected error occurred."}
                </p>

                <Link
                    to={redirectTo ?? fallbackRedirect}
                    className="bg-button-gradient inline-block px-6 py-2 rounded-sm text-white font-semibold hover:text-slate-200 transition-colors duration-150"
                >
                    {buttonText ?? fallbackButtonText}
                </Link>
            </div>
        </div>
    );
};

export default ErrorPage;
