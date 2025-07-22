import { useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import api from "../../api/api";
import SharedButtonActions from "../shared/SharedButtonActions.jsx";

const BucketListCard = ({ item, refreshBucketList }) => {
    const [isCompleted, setIsCompleted] = useState(item.completed);
    const [loading, setLoading] = useState(false);

    const { bucketListExperienceId, bucketListId, experience } = item;

    const handleToggle = async () => {
        try {
            setLoading(true);
            await api.put(`/bucketLists/${bucketListId}/bucketListExps/${bucketListExperienceId}/status`, {
                completed: !isCompleted,
            });
            setIsCompleted(!isCompleted);
            toast.success("Status updated");
            refreshBucketList();
        } catch (err) {
            console.error(err);
            toast.error("Failed to update");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            setLoading(true);
            await api.delete(`/bucketLists/${bucketListId}/bucketListExps/${bucketListExperienceId}`);
            toast.success("Experience removed");
            refreshBucketList();
        } catch (err) {
            console.error(err);
            toast.error("Failed to delete");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card">
            <img
                src={`${import.meta.env.VITE_BACK_END_URL}/images/${experience.experienceImage}`}
                alt={experience.experienceName}
                className="card-img"
            />
            <div className="card-body">
                <h3 className="text-lg font-semibold text-slate-800 mb-1">{experience.experienceName}</h3>
                <span className="inline-block bg-indigo-500 text-white text-xs px-2 py-1 rounded-full mb-2">
          {experience.categoryName}
        </span>
                <p className="text-sm text-gray-600 mb-4">Added by: {experience.createdBy}</p>

                <SharedButtonActions
                    layoutMode="bucketlist-card"
                    isCompleted={isCompleted}
                    onToggleCompleted={handleToggle}
                    onDelete={handleDelete}
                    item={item}
                />

            </div>
        </div>
    );
};

export default BucketListCard;
