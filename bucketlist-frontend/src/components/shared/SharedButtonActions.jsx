import { useState } from "react";
import { MdArrowBack, MdRemoveCircleOutline, MdCheckCircle, MdClose, MdEdit, MdDelete } from "react-icons/md";
import { IoIosInformationCircle } from "react-icons/io";
import ConfirmModal from "./ConfirmModal";
import { useNavigate, Link, useLocation } from "react-router-dom";
import bucketIcon from "../../assets/images/bucket-brand.png";

const SharedButtonActions = ({
                                 isCompleted = false,
                                 onToggleCompleted = null,
                                 onDelete = null,
                                 onAdd = null,
                                 showInfoLink = false,
                                 infoLink = "",
                                 item = null,
                                 isInBucketList = false,
                                 layoutMode = "default",
                                 showEdit = false,
                                 showDelete = false
                             }) => {
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState("");
    const navigate = useNavigate();
    const location = useLocation();

    const handleConfirm = () => {
        if (modalType === "toggle" && onToggleCompleted) onToggleCompleted();
        else if (modalType === "delete" && onDelete) onDelete();
        else if (modalType === "add" && onAdd) onAdd();
        setShowModal(false);
    };

    const renderButtons = () => {
        switch (layoutMode) {
            case "bucketlist-detail":
                return (
                    <>
                        <button
                            onClick={() => {
                                setModalType("toggle");
                                setShowModal(true);
                            }}
                            className={`btn-icon ${isCompleted ? "btn-incomplete" : "btn-completed"}`}
                            title={isCompleted ? "Mark as Incomplete" : "Mark as Completed"}
                            aria-label="Toggle Completed"
                        >
                            {isCompleted ? <MdClose /> : <MdCheckCircle />}
                        </button>

                        <button
                            onClick={() => navigate(-1)}
                            className="btn-back"
                            title="Back"
                            aria-label="Go Back"
                        >
                            <MdArrowBack />
                        </button>

                        {onDelete && (
                            <button
                                onClick={() => {
                                    setModalType("delete");
                                    setShowModal(true);
                                }}
                                className="btn-icon btn-incomplete"
                                title="Remove from Bucket List"
                            >
                                <MdRemoveCircleOutline />
                            </button>
                        )}
                    </>
                );

            case "experience-detail":
                return (
                    <>
                        <button
                            onClick={() => navigate(-1)}
                            className="btn-back"
                            title="Back"
                            aria-label="Go Back"
                        >
                            <MdArrowBack />
                        </button>

                        {(onAdd || onDelete) && (
                            <button
                                onClick={() => {
                                    setModalType(isInBucketList ? "delete" : "add");
                                    setShowModal(true);
                                }}
                                className={`btn-icon ${isInBucketList ? "btn-incomplete" : "btn-completed"}`}
                                title={isInBucketList ? "Remove from Bucket List" : "Add to Bucket List"}
                                aria-label="Toggle Bucket List"
                            >
                                {isInBucketList ? (
                                    <MdRemoveCircleOutline />
                                ) : (
                                    <img
                                        src={bucketIcon}
                                        alt="Add to Bucket List"
                                        className="h-7 w-7 object-contain"
                                    />
                                )}
                            </button>
                        )}
                    </>
                );

            case "user-created-experience-detail":
                const backPath = location.state?.from === "created-experiences"
                    ? "/profile#created-experiences"
                    : -1;

                return (
                    <>
                        <Link
                            to={`/experiences/edit/${item?.experienceId}`}
                            className="btn-icon btn-edit"
                            title="Edit"
                            aria-label="Edit"
                        >
                            <MdEdit />
                        </Link>

                        <button
                            onClick={() => navigate(backPath)}
                            className="btn-back"
                            title="Back"
                            aria-label="Go Back"
                        >
                            <MdArrowBack />
                        </button>

                        {onDelete && (
                            <button
                                onClick={() => {
                                    setModalType("delete");
                                    setShowModal(true);
                                }}
                                className="btn-icon btn-delete"
                                title="Delete"
                                aria-label="Delete"
                            >
                                <MdDelete />
                            </button>
                        )}
                    </>
                );

            case "bucketlist-card":
                return (
                    <>
                        <button
                            onClick={() => {
                                setModalType("toggle");
                                setShowModal(true);
                            }}
                            className={`btn-icon ${isCompleted ? "btn-incomplete" : "btn-completed"}`}
                            title={isCompleted ? "Mark as Incomplete" : "Mark as Completed"}
                            aria-label="Toggle Completed"
                        >
                            {isCompleted ? <MdClose /> : <MdCheckCircle />}
                        </button>

                        <Link
                            to={`/bucketlist/details/${item.bucketListExperienceId}`}
                            state={{ item }}
                            className="btn-icon btn-info"
                            title="View Info"
                            aria-label="View Info"
                        >
                            <IoIosInformationCircle />
                        </Link>

                        <button
                            onClick={() => {
                                setModalType("delete");
                                setShowModal(true);
                            }}
                            className="btn-icon btn-incomplete"
                            title="Remove from Bucket List"
                            aria-label="Remove from Bucket List"
                        >
                            <MdRemoveCircleOutline />
                        </button>
                    </>
                );

            case "experience-card":
                return (
                    <>
                        {showEdit && (
                            <button
                                className="btn-icon btn-edit"
                                title="Edit"
                                aria-label="Edit Experience"
                                onClick={() =>
                                    navigate(`/experiences/edit/${item?.experienceId}`, {
                                        state: { from: "created-experiences" },
                                    })
                                }
                            >
                                <MdEdit />
                            </button>
                        )}

                        <button
                            className="btn-icon btn-info"
                            title="Info"
                            aria-label="Experience information"
                            onClick={() =>
                                navigate(`/experiences/${item?.experienceId}`, {
                                    state: { from: "created-experiences" },
                                })
                            }
                        >
                            <IoIosInformationCircle />
                        </button>

                        {showDelete && (
                            <button
                                className="btn-icon btn-delete"
                                title="Delete"
                                aria-label="Delete Experience"
                                onClick={() => setModalType("delete") || setShowModal(true)}
                            >
                                <MdDelete />
                            </button>
                        )}
                    </>
                );

            case "guest-experience-detail":
                return (
                    <>
                        <button
                            onClick={() => navigate(-1)}
                            className="btn-back"
                            title="Back"
                            aria-label="Go Back"
                        >
                            <MdArrowBack />
                        </button>
                    </>
                );

            default:
                return (
                    <>
                        {showInfoLink && (
                            typeof infoLink === "function" ? (
                                <button
                                    onClick={infoLink}
                                    className="btn-icon btn-info"
                                    title="View Info"
                                >
                                    <IoIosInformationCircle />
                                </button>
                            ) : (
                                <Link
                                    to={infoLink}
                                    state={{ item, from: "experiencesPage" }}
                                    className="btn-icon btn-info"
                                >
                                    <IoIosInformationCircle />
                                </Link>
                            )
                        )}

                        {(onAdd || onDelete) && (
                            <button
                                onClick={() => {
                                    setModalType(isInBucketList ? "delete" : "add");
                                    setShowModal(true);
                                }}
                                className={`btn-icon ${isInBucketList ? "btn-incomplete" : "btn-completed"}`}
                                title={isInBucketList ? "Remove from Bucket List" : "Add to Bucket List"}
                            >
                                {isInBucketList ? (
                                    <MdRemoveCircleOutline />
                                ) : (
                                    <img
                                        src={bucketIcon}
                                        alt="Add to Bucket List"
                                        className="h-7 w-7 object-contain"
                                    />
                                )}
                            </button>
                        )}
                    </>
                );
        }
    };

    return (
        <>
            <div className="flex justify-center gap-6 text-3xl mb-4 items-center">
                {renderButtons()}
            </div>

            <ConfirmModal
                show={showModal}
                message={
                    modalType === "delete"
                        ? "Are you sure you want to remove this experience from your Bucket List?"
                        : modalType === "add"
                            ? "Add this experience to your bucket list?"
                            : isCompleted
                                ? "Mark this experience as incomplete?"
                                : "Mark this experience as completed?"
                }
                onConfirm={handleConfirm}
                onCancel={() => setShowModal(false)}
            />
        </>
    );
};

export default SharedButtonActions;
