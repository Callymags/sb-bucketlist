import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import { fetchSingleExperience } from "../../store/actions";
import api from "../../api/api";
import toast from "react-hot-toast";
import AOS from "aos";
import "aos/dist/aos.css";

const EditExperience = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { singleExperience, isLoading } = useSelector((state) => state.experiences);
    const [imageFile, setImageFile] = useState(null);

    const [formData, setFormData] = useState({
        experienceName: "",
        description: "",
        experienceImage: "",
    });

    useEffect(() => {
        AOS.init({ duration: 1000, once: true });
    }, []);

    useEffect(() => {
        dispatch(fetchSingleExperience(id));
    }, [dispatch, id]);

    useEffect(() => {
        if (singleExperience) {
            setFormData({
                experienceName: singleExperience.experienceName || "",
                description: singleExperience.description || "",
                experienceImage: singleExperience.experienceImage || "",
            });
        }
    }, [singleExperience]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            await api.put(`/experience/${id}`, formData);

            if (imageFile) {
                const imageFormData = new FormData();
                imageFormData.append("image", imageFile);
                await api.put(`/experience/${id}/image`, imageFormData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
            }

            toast.success("Experience updated!");
            navigate("/profile");
        } catch (error) {
            console.error("Update failed", error);
            toast.error("Failed to update experience");
        }
    };

    if (isLoading || !singleExperience) return <p className="text-center mt-6">Loading...</p>;

    return (
        <div className="max-w-2xl mx-auto p-6">
            <h1
                className="text-xl font-bold uppercase tracking-widest text-slate-800 border-b border-slate-300 pb-2 mb-6"
                data-aos="fade-down"
            >
                Edit Experience
            </h1>
            <form onSubmit={handleSubmit} className="space-y-4" data-aos="fade-up" data-aos-delay="100">
                <div>
                    <label className="block mb-1">Name</label>
                    <input
                        type="text"
                        name="experienceName"
                        value={formData.experienceName}
                        onChange={handleChange}
                        maxLength={80}
                        className="w-full border border-gray-300 rounded px-3 py-2"
                        required
                    />
                </div>

                <div>
                    <label className="block mb-1">Description</label>
                    <textarea
                        name="description"
                        rows="4"
                        value={formData.description}
                        onChange={handleChange}
                        className="w-full border border-gray-300 rounded px-3 py-2"
                    />
                </div>

                <div>
                    <label className="block mb-1">Image Upload</label>

                    {formData.experienceImage && (
                        <img
                            src={`${import.meta.env.VITE_BACK_END_URL}/images/${formData.experienceImage}`}
                            alt="Current"
                            className="w-40 h-28 object-cover mb-4 rounded"
                        />
                    )}

                    <input
                        type="file"
                        name="experienceImage"
                        accept="image/*"
                        onChange={(e) => {
                            setImageFile(e.target.files[0]);
                            setFormData((prev) => ({ ...prev, experienceImage: "" }));
                        }}
                        className="block"
                    />
                </div>

                <button
                    type="submit"
                    className="bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600"
                >
                    Update Experience
                </button>
            </form>
        </div>
    );
};

export default EditExperience;
