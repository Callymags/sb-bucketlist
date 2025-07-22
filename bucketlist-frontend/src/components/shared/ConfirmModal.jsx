import { createPortal } from "react-dom";

const ConfirmModal = ({ show, message, onConfirm, onCancel }) => {
    if (!show) return null;

    return createPortal(
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-[9999]">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm text-center">
                <p className="mb-6 text-[#101112] font-medium">{message}</p>
                <div className="flex justify-center gap-4">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 rounded bg-[#1a2238] text-[#f8f8fb] hover:bg-opacity-90 transition"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 rounded bg-[#ff6a3d] text-[#f8f8fb] hover:bg-opacity-90 transition"
                    >
                        Confirm
                    </button>
                </div>
            </div>
        </div>,
        document.getElementById("modal-root")
    );
};

export default ConfirmModal;
