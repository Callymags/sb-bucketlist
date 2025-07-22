import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import toast from "react-hot-toast";
import api from "../../api/api";
import SharedButtonActions from "../shared/SharedButtonActions";

const BucketListDetail = ({ refreshBucketList }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const item = location.state?.item;

    const [isCompleted, setIsCompleted] = useState(item?.completed || false);
    const [loading, setLoading] = useState(false);

    if (!item) {
        return (
            <div className="text-center mt-10 text-red-600">
                No bucket list experience found.
            </div>
        );
    }

    const { experience } = item;

    const handleToggle = async () => {
        try {
            setLoading(true);
            await api.put(
                `/bucketLists/${item.bucketListId}/bucketListExps/${item.bucketListExperienceId}/status`,
                { completed: !isCompleted }
            );
            setIsCompleted(!isCompleted);
            toast.success("Status updated");
            refreshBucketList?.();
        } catch (err) {
            console.error(err);
            toast.error("Failed to update status");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            setLoading(true);
            await api.delete(
                `/bucketLists/${item.bucketListId}/bucketListExps/${item.bucketListExperienceId}`
            );
            toast.success("Experience removed");
            navigate(-1);
        } catch (err) {
            console.error(err);
            toast.error("Failed to delete");
        } finally {
            setLoading(false);
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

                <SharedButtonActions
                    layoutMode="bucketlist-detail"
                    isCompleted={isCompleted}
                    onToggleCompleted={handleToggle}
                    onDelete={handleDelete}
                    isInBucketList={true}
                    item={experience}
                />
            </div>
        </div>
    );
};

export default BucketListDetail;
