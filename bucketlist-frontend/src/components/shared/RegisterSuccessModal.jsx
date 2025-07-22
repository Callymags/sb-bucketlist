// components/shared/SuccessModal.jsx
import React from "react";

const RegisterSuccessModal = ({ show, message, onClose }) => {
    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm text-center">
                <p className="mb-6 text-[#101112] font-medium">{message}</p>
                <button
                    onClick={onClose}
                    className="px-4 py-2 rounded bg-[#ff6a3d] text-[#f8f8fb] hover:bg-opacity-90 transition"
                >
                    Go to Login
                </button>
            </div>
        </div>
    );
};

export default RegisterSuccessModal;
