import { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../../api/api";
import AOS from "aos";
import "aos/dist/aos.css";

const CreateExperience = () => {
    const { user } = useSelector((state) => state.auth);
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        experienceName: "",
        description: "",
        categoryId: "",
        image: null
    });

    const [categories, setCategories] = useState([]);

    useEffect(() => {
        AOS.init({ duration: 1000, once: true });
    }, []);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await api.get("/categories", {
                    params: { pageNumber: 0, pageSize: 100, sortBy: "categoryName", sortOrder: "asc" }
                });

                setCategories(response.data.content || []);
            } catch (error) {
                console.error("Failed to fetch categories", error);
                toast.error("Error loading categories");
            }
        };

        fetchCategories();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleImageChange = (e) => {
        setFormData((prev) => ({ ...prev, image: e.target.files[0] }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.categoryId) {
            toast.error("Please select a category");
            return;
        }

        try {
            const { experienceName, description, categoryId } = formData;
            const res = await api.post(`/categories/${categoryId}/experience`, {
                experienceName,
                description,
            });

            const experienceId = res.data.experienceId;

            if (formData.image) {
                const imgData = new FormData();
                imgData.append("image", formData.image);
                await api.put(`/experience/${experienceId}/image`, imgData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
            }

            toast.success("Experience created!");
            navigate("/profile");
        } catch (error) {
            console.error(error);
            toast.error("Failed to create experience");
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-6">
            <h1
                className="text-xl font-bold uppercase tracking-widest text-slate-800 border-b border-slate-300 pb-2 mb-6"
                data-aos="fade-down"
            >
                Create New Experience
            </h1>
            <form onSubmit={handleSubmit} className="space-y-4" data-aos="fade-up" data-aos-delay="100">
                <div>
                    <label className="block mb-1">Name</label>
                    <input
                        type="text"
                        name="experienceName"
                        value={formData.experienceName}
                        onChange={handleChange}
                        required
                        maxLength={80}
                        className="w-full border border-gray-300 rounded px-3 py-2"
                    />
                </div>

                <div>
                    <label className="block mb-1">Description</label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        required
                        className="w-full border border-gray-300 rounded px-3 py-2"
                    />
                </div>

                <div>
                    <label className="block mb-1">Category</label>
                    <select
                        name="categoryId"
                        value={formData.categoryId}
                        onChange={handleChange}
                        className="w-full border border-gray-300 rounded px-3 py-2"
                        required
                    >
                        <option value="">Select a Category</option>
                        {Array.isArray(categories) &&
                            categories.map((cat) => (
                                <option key={cat.categoryId} value={cat.categoryId}>
                                    {cat.categoryName}
                                </option>
                            ))}
                    </select>
                </div>

                <div>
                    <label className="block mb-1">Image Upload</label>
                    <input
                        type="file"
                        name="image"
                        accept="image/*"
                        onChange={handleImageChange}
                        className="block"
                    />
                </div>

                <button
                    type="submit"
                    className="bg-[#ff6a3d] text-[#f8f8fb] px-4 py-2 rounded hover:bg-opacity-90 transition"
                >
                    Create Experience
                </button>
            </form>
        </div>
    );
};

export default CreateExperience;
