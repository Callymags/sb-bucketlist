import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import api from "../../api/api";
import { useState, useEffect } from "react";
import { fetchUserBucketList } from "../../store/actions";
import SharedButtonActions from "../shared/SharedButtonActions";
import { useNavigate } from "react-router-dom";

const ExperienceCard = ({ experience }) => {
    const dispatch = useDispatch();
    const { user } = useSelector((state) => state.auth);
    const { bucketList } = useSelector((state) => state.profile);

    const [loading, setLoading] = useState(false);
    const [isInBucketList, setIsInBucketList] = useState(false);
    const navigate = useNavigate();

    // Sync with bucketList whenever it updates
    useEffect(() => {
        const isInList = bucketList?.bucketListExps?.some(
            (exp) => exp.experience?.experienceId === experience.experienceId
        );
        setIsInBucketList(isInList);
    }, [bucketList, experience.experienceId]);

    if (!experience) return null;

    const handleAdd = async () => {
        try {
            setLoading(true);
            await api.post(`/bucketLists/experiences/${experience.experienceId}`);
            toast.success("Added to your bucket list!");
            dispatch(fetchUserBucketList(user.id)); // this will re-trigger useEffect
        } catch (err) {
            toast.error("Failed to add experience");
            console.error("❌ handleAdd error:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleRemove = async () => {
        try {
            setLoading(true);
            const item = bucketList.bucketListExps.find(
                (exp) => exp.experience?.experienceId === experience.experienceId
            );
            if (!item) throw new Error("Experience not found in bucket list.");
            await api.delete(`/bucketLists/${item.bucketListId}/bucketListExps/${item.bucketListExperienceId}`);
            toast.success("Removed from bucket list!");
            dispatch(fetchUserBucketList(user.id));
        } catch (err) {
            toast.error("Failed to remove experience");
            console.error("❌ handleRemove error:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleInfoClick = () => {
        navigate(`/bucketlist/details/${experience.experienceId}`, {
            state: { experience },
        });
    };

    return (
        <div className="card">
            <img
                src={`${import.meta.env.VITE_BACK_END_URL}/images/${experience.experienceImage || "default.jpg"}`}
                alt={experience.experienceName}
                className="card-img"
            />
            <div className="card-body">
                <h3 className="text-lg font-semibold text-slate-800 mb-1">{experience.experienceName}</h3>
                <span className="inline-block bg-indigo-500 text-white text-xs px-2 py-1 rounded-full mb-2">
                    {experience.categoryName}
                </span>
                <p className="text-sm text-gray-600 mb-4">Added by: {experience.createdBy}</p>

                {user ? (
                    <SharedButtonActions
                        viewType="card"
                        onAdd={!isInBucketList ? handleAdd : null}
                        onDelete={isInBucketList ? handleRemove : null}
                        showInfoLink={true}
                        infoLink={`/experiences/${experience.experienceId}`}
                        item={experience}
                        isInBucketList={isInBucketList}
                    />
                ) : (
                    <SharedButtonActions
                        viewType="card"
                        showInfoLink={true}
                        infoLink={`/experiences/${experience.experienceId}`}
                        item={experience}
                    />
                )}
            </div>
        </div>
    );
};

export default ExperienceCard;
