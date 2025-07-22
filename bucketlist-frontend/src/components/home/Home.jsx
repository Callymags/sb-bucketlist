import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchExperiences } from "../../store/actions";
import Loader from "../shared/Loader";
import ExperienceCard from "../experiences/ExperienceCard.jsx";
import AOS from 'aos';
import 'aos/dist/aos.css';

// Image assets
import heroImage from "../../assets/images/hot-air-balloon.jpg";
import registerIcon from "../../assets/images/clipboard-dinosoftlabs.png";
import searchIcon from "../../assets/images/search-good-ware.png";
import addIcon from "../../assets/images/add-roundicons.png";
import ideaIcon from "../../assets/images/idea-freepik.png";

const Home = () => {
    const dispatch = useDispatch();
    const { experiences, isLoading, errorMessage } = useSelector((state) => state.experiences);

    useEffect(() => {
        dispatch(fetchExperiences({
            page: 0,
            size: 4,
            sortBy: "experienceId",
            sortOrder: "desc"
        }));
        AOS.init({ duration: 800, once: true });
    }, [dispatch]);

    return (
        <div className="home-wrapper">
            {/* Hero Section */}
            <div
                className="text-white text-center flex items-center justify-center"
                style={{
                    backgroundImage: `url(${heroImage})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    backgroundRepeat: "no-repeat",
                    height: "60vh",
                    padding: "4rem 1rem",
                    textShadow: "1px 1px 4px rgba(0,0,0,0.8)",
                }}
            >
                <h1 className="text-3xl md:text-5xl font-bold max-w-2xl">
                    The perfect place to find and share bucket list ideas!
                </h1>
            </div>

            {/* How it Works */}
            <div className="text-center mt-10 px-4">
                <h1 className="subheading text-2xl md:text-3xl font-bold mb-8" data-aos="fade-up">
                    How it works
                </h1>

                {/* Step 1 */}
                <div className="w-full flex justify-center my-10 px-4" data-aos="fade-up">
                    <div className="flex flex-col md:flex-row items-center gap-6 max-w-3xl w-full">
                        <img src={registerIcon} alt="Register icon" className="h-36 w-auto" />
                        <div className="text-center md:text-left">
                            <h5 className="text-xl font-semibold mb-2">Register</h5>
                            <p>Register to create your profile and personalized bucket list.</p>
                        </div>
                    </div>
                </div>
                <hr />

                {/* Step 2 */}
                <div className="w-full flex justify-center my-10 px-4" data-aos="fade-left">
                    <div className="flex flex-col md:flex-row-reverse items-center gap-6 max-w-3xl w-full">
                        <img src={searchIcon} alt="Search icon" className="h-36 w-auto" />
                        <div className="text-center md:text-left">
                            <h5 className="text-xl font-semibold mb-2">Search Experiences</h5>
                            <p>Browse and filter experiences by category, name, or recency.</p>
                        </div>
                    </div>
                </div>
                <hr />

                {/* Step 3 */}
                <div className="w-full flex justify-center my-10 px-4" data-aos="fade-right">
                    <div className="flex flex-col md:flex-row items-center gap-6 max-w-3xl w-full">
                        <img src={addIcon} alt="Add icon" className="h-36 w-auto" />
                        <div className="text-center md:text-left">
                            <h5 className="text-xl font-semibold mb-2">Add to Bucket List</h5>
                            <p>Add experiences to your personal list to track progress.</p>
                        </div>
                    </div>
                </div>
                <hr />

                {/* Step 4 */}
                <div className="w-full flex justify-center my-10 px-4" data-aos="fade-left">
                    <div className="flex flex-col md:flex-row-reverse items-center gap-6 max-w-3xl w-full">
                        <img src={ideaIcon} alt="Create icon" className="h-36 w-auto" />
                        <div className="text-center md:text-left">
                            <h5 className="text-xl font-semibold mb-2">Create Experiences</h5>
                            <p>Contribute by adding your own experience ideas.</p>
                        </div>
                    </div>
                </div>
                <hr />
            </div>

            {/* Latest ExperiencesCard */}
            <div className="container mx-auto px-4 my-10" data-aos="fade-up">
                <h1 className="text-center subheading text-2xl font-bold mb-6">Latest Entries</h1>

                {isLoading ? (
                    <Loader />
                ) : errorMessage ? (
                    <div className="text-center text-danger">{errorMessage}</div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                        {Array.isArray(experiences) && experiences.length > 0 ? (
                            experiences.slice(0, 4).map((experience) => (
                                <ExperienceCard key={experience.experienceId} experience={experience} />
                            ))
                        ) : (
                            <p className="text-center text-gray-600">No recent experiences found.</p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Home;
