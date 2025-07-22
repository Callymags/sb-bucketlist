import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
    fetchExperiences,
    fetchUserBucketList,
    fetchCategories
} from "../../store/actions";
import ExperienceCard from "./ExperienceCard";
import Loader from "../shared/Loader";
import Paginations from "../shared/Paginations";
import ErrorPage from "../shared/ErrorPage";
import { FaExclamationTriangle } from "react-icons/fa";
import AOS from "aos";
import "aos/dist/aos.css";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const ExperiencesPage = () => {
    const dispatch = useDispatch();
    const location = useLocation();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    const experiences = useSelector(state => state.experiences.experiences);
    const isLoading = useSelector(state => state.experiences.isLoading);
    const errorMessage = useSelector(state => state.experiences.errorMessage);
    const totalPages = useSelector(state => state.experiences.totalPages);
    const categories = useSelector(state => state.categories?.categories || []);
    const { user } = useSelector(state => state.auth);

    const page = parseInt(searchParams.get("page")) || 1;
    const [sortOrder, setSortOrder] = useState(searchParams.get("sortby") || "desc");
    const [categoryId, setCategoryId] = useState(searchParams.get("category") || "");
    const [keyword, setKeyword] = useState(searchParams.get("keyword") || "");

    const filters = {
        page: page - 1,
        size: 8,
        sortBy: "experienceId",
        sortOrder,
        categoryId,
        keyword
    };

    const hasExperiences = Array.isArray(experiences) && experiences.length > 0;

    useEffect(() => {
        AOS.init({ duration: 1500, once: true });
    }, []);

    useEffect(() => {
        dispatch(fetchExperiences(filters));
        dispatch(fetchCategories());
        if (user?.id) {
            dispatch(fetchUserBucketList(user.id));
        }
    }, [dispatch, location.search, user?.id]);

    return (
        <div className="p-4">
            <h1
                className="text-2xl font-bold uppercase tracking-widest text-slate-800 border-b border-slate-300 pb-2 mb-6 text-center"
                data-aos="fade-down"
            >
                Explore Experiences
            </h1>

            <div className="flex flex-wrap gap-4 mb-6 justify-center" data-aos="fade-up" data-aos-delay="100">
                {/* Category Filter */}
                <select
                    value={categoryId}
                    onChange={(e) => setCategoryId(e.target.value)}
                    className="px-4 py-2 border border-gray-300 rounded-md text-sm"
                >
                    <option value="">All Categories</option>
                    {categories.map((cat) => (
                        <option key={cat.categoryId} value={cat.categoryId}>
                            {cat.categoryName}
                        </option>
                    ))}
                </select>

                {/* Sort Order Filter */}
                <select
                    value={sortOrder}
                    onChange={(e) => setSortOrder(e.target.value)}
                    className="px-4 py-2 border border-gray-300 rounded-md text-sm"
                >
                    <option value="">Sort By</option>
                    <option value="desc">Newest First</option>
                    <option value="asc">Oldest First</option>
                </select>

                {/* Apply Filters Button */}
                <button
                    onClick={() => {
                        const params = new URLSearchParams();

                        if (categoryId) params.set("category", categoryId);
                        if (sortOrder) params.set("sortby", sortOrder);
                        params.set("page", "1"); // reset to first page

                        navigate(`/experiences?${params.toString()}`);
                    }}
                    className="px-5 py-2 text-white rounded-lg bg-button-gradient hover:opacity-90 transition text-sm"
                >
                    Apply Filters
                </button>
            </div>

            {/* Content Rendering */}
            {isLoading ? (
                <Loader />
            ) : totalPages > 0 && page > totalPages ? (
                <ErrorPage
                    message="You've gone beyond the available pages for this category. Please try a lower page number."
                    redirectTo="/experiences"
                    buttonText="Return to Experiences"
                />
            ) : errorMessage ? (
                    <ErrorPage
                        message={errorMessage}
                        redirectTo="/experiences"
                        buttonText={
                            errorMessage.includes("beyond the available pages")
                                ? "Back to Explore"
                                : "Return to Home"
                        }
                    />
                )
                : hasExperiences ? (
                <>
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6" data-aos="fade-up">
                        {experiences.map((exp, index) => (
                            <div key={exp.experienceId} data-aos="zoom-in" data-aos-delay={index * 100}>
                                <ExperienceCard experience={exp} />
                            </div>
                        ))}
                    </div>

                    {totalPages > 1 && (
                        <div className="flex justify-center mt-10">
                            <Paginations numberOfPage={totalPages} currentPage={page} />
                        </div>
                    )}
                </>
            ) : (
                <div className="min-h-[calc(100vh-64px)] flex flex-col items-center justify-center py-10 px-4">
                    <FaExclamationTriangle className="text-6xl text-orange-500 mb-6" />
                    <h2 className="text-2xl font-bold text-slate-800 mb-4 text-center">No experiences found</h2>
                    <p className="text-gray-600 mb-6 text-center">
                        Try adjusting your filters or search again.
                    </p>
                    <button
                        onClick={() => {
                            setCategoryId("");
                            setSortOrder("desc");
                            setKeyword("");
                            navigate("/experiences");
                        }}
                        className="bg-button-gradient inline-block px-6 py-2 rounded-sm text-white font-semibold hover:text-slate-200 transition-colors duration-150"
                    >
                        Go to First Page
                    </button>
                </div>
            )}
        </div>
    );
};

export default ExperiencesPage;
