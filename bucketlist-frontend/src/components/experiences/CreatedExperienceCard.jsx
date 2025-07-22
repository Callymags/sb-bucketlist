import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../../api/api";
import { fetchUserBucketList } from "../../store/actions";
import SharedButtonActions from "../shared/SharedButtonActions";


const CreatedExperienceCard = ({ experience, refreshExperiences }) => {
    const dispatch = useDispatch();
    const { user } = useSelector((state) => state.auth);

    const handleDelete = async () => {
        try {
            await api.delete(`/experience/${experience.experienceId}`);
            toast.success("Experience deleted");
            refreshExperiences();
            dispatch(fetchUserBucketList(user.id));
        } catch (error) {
            console.error(error);
            toast.error("Failed to delete experience");
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
                    layoutMode="experience-card"
                    item={experience}
                    onDelete={handleDelete}
                    showEdit={true}
                    showDelete={true}
                />

            </div>
        </div>
    );
};

export default CreatedExperienceCard;
