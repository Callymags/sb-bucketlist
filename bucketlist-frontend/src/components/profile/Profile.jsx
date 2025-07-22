import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchUserExperiences, fetchUserBucketList } from "../../store/actions";
import BucketListCard from "../bucketlist/BucketListCard";
import CreatedExperienceCard from "../experiences/CreatedExperienceCard";
import ConfirmModal from "../shared/ConfirmModal";
import Paginations from "../shared/Paginations";
import { Link } from "react-router-dom";
import AOS from "aos";
import "aos/dist/aos.css";

const Profile = () => {
    const dispatch = useDispatch();
    const { user } = useSelector((state) => state.auth);
    const { createdExperiences, bucketList } = useSelector((state) => state.profile);

    const completed = bucketList?.bucketListExps?.filter((exp) => exp.completed) || [];
    const notCompleted = bucketList?.bucketListExps?.filter((exp) => !exp.completed) || [];

    // Independent pagination state for each section
    const [todoPage, setTodoPage] = useState(1);
    const [completedPage, setCompletedPage] = useState(1);
    const [createdExpPage, setCreatedExpPage] = useState(1);
    const itemsPerPage = 4;

    const paginatedToDo = notCompleted.slice((todoPage - 1) * itemsPerPage, todoPage * itemsPerPage);
    const paginatedCompleted = completed.slice((completedPage - 1) * itemsPerPage, completedPage * itemsPerPage);

    const [showModal, setShowModal] = useState(false);
    const [modalContent, setModalContent] = useState({ message: "", onConfirm: () => {} });

    useEffect(() => {
        if (user?.id) {
            dispatch(fetchUserExperiences(user.id, createdExpPage - 1, itemsPerPage)); // Backend paginated
            dispatch(fetchUserBucketList(user.id));
        }
        AOS.init({ duration: 600, once: true });
    }, [dispatch, user, createdExpPage]);

    useEffect(() => {
        setTodoPage(1);
        setCompletedPage(1);
    }, [bucketList?.bucketListExps]);

    const openModal = (message, onConfirm) => {
        setModalContent({ message, onConfirm });
        setShowModal(true);
    };

    const closeModal = () => setShowModal(false);

    return (
        <div className="p-4">
            {/* Bucket List Section */}
            <section className="mb-10" data-aos="fade-up">
                <h2 className="text-xl font-bold uppercase tracking-widest text-slate-800 border-b border-slate-300 pb-2 mb-6">
                    My Bucket List
                </h2>

                {notCompleted.length > 0 && (
                    <>
                        <h3 className="text-lg font-semibold text-slate-600 mb-3">To Do</h3>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {paginatedToDo.map((item, index) => (
                                <div
                                    key={item.bucketListExperienceId}
                                    data-aos="fade-up"
                                    data-aos-delay={index * 100}
                                >
                                    <BucketListCard
                                        item={item}
                                        openModal={openModal}
                                        refreshBucketList={() => dispatch(fetchUserBucketList(user.id))}
                                    />
                                </div>
                            ))}
                        </div>

                        {notCompleted.length > itemsPerPage && (
                            <div className="flex justify-center mt-4">
                                <Paginations
                                    numberOfPage={Math.ceil(notCompleted.length / itemsPerPage)}
                                    currentPage={todoPage}
                                    onPageChange={setTodoPage}
                                />
                            </div>
                        )}
                    </>
                )}

                {completed.length > 0 && (
                    <>
                        <h3 className="text-lg font-semibold text-slate-600 mt-8 mb-3">Completed</h3>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {paginatedCompleted.map((item, index) => (
                                <div
                                    key={item.bucketListExperienceId}
                                    data-aos="fade-up"
                                    data-aos-delay={index * 100}
                                >
                                    <BucketListCard
                                        item={item}
                                        openModal={openModal}
                                        refreshBucketList={() => dispatch(fetchUserBucketList(user.id))}
                                    />
                                </div>
                            ))}
                        </div>

                        {completed.length > itemsPerPage && (
                            <div className="flex justify-center mt-4">
                                <Paginations
                                    numberOfPage={Math.ceil(completed.length / itemsPerPage)}
                                    currentPage={completedPage}
                                    onPageChange={setCompletedPage}
                                />
                            </div>
                        )}
                    </>
                )}

                {notCompleted.length === 0 && completed.length === 0 && (
                    <div className="text-center mt-4" data-aos="fade-in">
                        <p className="text-gray-600 mb-4">No items in your bucket list yet.</p>
                        <Link
                            to="/experiences"
                            className="inline-block px-6 py-2 bg-[#ff6a3d] text-[#f8f8fb] text-sm font-medium rounded-lg hover:bg-opacity-90 transition"
                        >
                            + Browse Experiences
                        </Link>
                    </div>
                )}
            </section>

            {/* Created Experiences Section */}
            <section className="mb-8" data-aos="fade-up" data-aos-delay="300">
                <h2 className="text-xl font-bold uppercase tracking-widest text-slate-800 border-b border-slate-300 pb-2 mb-6">
                    My Created Experiences
                </h2>

                {createdExperiences?.content?.length > 0 ? (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {createdExperiences.content.map((exp, index) => (
                                <div
                                    key={exp.experienceId}
                                    data-aos="fade-up"
                                    data-aos-delay={index * 100}
                                >
                                    <CreatedExperienceCard
                                        experience={exp}
                                        refreshExperiences={() => dispatch(fetchUserExperiences(user.id))}
                                    />
                                </div>
                            ))}
                        </div>

                        {createdExperiences?.totalPages > 1 && (
                            <div className="flex justify-center mt-10">
                                <Paginations
                                    numberOfPage={createdExperiences.totalPages}
                                    currentPage={createdExpPage}
                                    onPageChange={setCreatedExpPage}
                                />
                            </div>
                        )}
                    </>
                ) : (
                    <div className="text-center mt-4" data-aos="fade-in">
                        <p className="text-gray-600 mb-4">No experiences created yet.</p>
                        <Link
                            to="/experiences/create"
                            className="inline-block px-6 py-2 bg-[#ff6a3d] text-[#f8f8fb] text-sm font-medium rounded-lg hover:bg-opacity-90 transition"
                        >
                            + Create Experience
                        </Link>
                    </div>
                )}
            </section>

            {/* Global Confirm Modal */}
            <ConfirmModal
                show={showModal}
                message={modalContent.message}
                onConfirm={() => {
                    modalContent.onConfirm();
                    closeModal();
                }}
                onCancel={closeModal}
            />
        </div>
    );
};

export default Profile;
