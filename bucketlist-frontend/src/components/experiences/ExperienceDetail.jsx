import { useParams, useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import api from "../../api/api";
import SharedButtonActions from "../shared/SharedButtonActions";
import Loader from "../shared/Loader";
import {
    fetchSingleExperience,
    fetchUserBucketList,
} from "../../store/actions";

const ExperienceDetail = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from;

    const { singleExperience: experience, isLoading, errorMessage } = useSelector((state) => state.experiences);
    const { user } = useSelector((state) => state.auth);
    const { bucketList } = useSelector((state) => state.profile);

    const [isInBucketList, setIsInBucketList] = useState(false);

    useEffect(() => {
        dispatch(fetchSingleExperience(id));
        dispatch(fetchUserBucketList(user?.id));
    }, [dispatch, id, user?.id]);

    useEffect(() => {
        if (bucketList && experience) {
            const exists = bucketList.bucketListExps?.some(
                (exp) => exp.experience?.experienceId === experience.experienceId
            );
            setIsInBucketList(!!exists);
        }
    }, [bucketList, experience]);

    if (isLoading) return <Loader />;
    if (errorMessage) return <p className="text-red-600 text-center mt-6">{errorMessage}</p>;
    if (!experience) return <p className="text-center mt-6">No experience found.</p>;

    const canModify =
        user?.username === experience.createdBy ||
        user?.roles?.includes("ROLE_ADMIN");

    const handleAdd = async () => {
        try {
            await api.post(`/bucketLists/experiences/${experience.experienceId}`);
            toast.success("Added to your bucket list!");
            dispatch(fetchUserBucketList(user.id));
        } catch {
            toast.error("Failed to add experience");
        }
    };

    const handleRemove = async () => {
        try {
            const item = bucketList.bucketListExps.find(
                (exp) => exp.experience?.experienceId === experience.experienceId
            );
            if (!item) throw new Error("Experience not found in bucket list.");
            await api.delete(`/bucketLists/${item.bucketListId}/bucketListExps/${item.bucketListExperienceId}`);
            toast.success("Removed from bucket list!");
            dispatch(fetchUserBucketList(user.id));
        } catch {
            toast.error("Failed to remove experience");
        }
    };

    const handleDelete = async () => {
        try {
            await api.delete(`/experience/${experience.experienceId}`);
            toast.success("Experience deleted");
            dispatch(fetchUserBucketList(user.id));
            navigate("/profile");
        } catch {
            toast.error("Failed to delete experience");
        }
    };

    return (
        <div className="flex justify-center mt-10 px-4">
            <div
                className="max-w-3xl w-full bg-white rounded-lg shadow p-6 text-center"
                data-aos="zoom-in"
                data-aos-duration="1200"
                data-aos-easing="ease-in-out"
            >
                <img
                    src={`${import.meta.env.VITE_IMAGE_BASE_URL}/${experience.experienceImage}`}
                    alt={experience.experienceName}
                    className="w-full h-64 object-cover rounded mb-6"
                />
                <h1 className="text-2xl font-bold mb-2">{experience.experienceName}</h1>
                <span className="inline-block bg-indigo-500 text-white text-xs px-2 py-1 rounded-full mb-2">
                    {experience.categoryName}
                </span>
                <p className="mb-4 text-gray-800">{experience.description}</p>
                <p className="text-xs italic text-gray-500 mb-6">
                    Created by: {experience.createdBy}
                </p>

                {user ? (
                    <SharedButtonActions
                        layoutMode={from === "created-experiences" ? "user-created-experience-detail" : "experience-detail"}
                        onAdd={from !== "created-experiences" && !isInBucketList ? handleAdd : null}
                        onDelete={
                            from === "created-experiences"
                                ? handleDelete
                                : isInBucketList
                                    ? handleRemove
                                    : null
                        }
                        item={experience}
                        isInBucketList={isInBucketList}
                    />
                ) : (
                    <SharedButtonActions
                        layoutMode="guest-experience-detail"
                        item={experience}
                    />
                )}
            </div>
        </div>
    );
};

export default ExperienceDetail;
