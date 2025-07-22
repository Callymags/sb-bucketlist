import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { AiOutlineLogin } from "react-icons/ai";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../shared/InputField";
import { useDispatch } from "react-redux";
import { authenticateSignInUser } from "../../store/actions";
import toast from "react-hot-toast";
import Spinners from "../shared/Spinners";
import AOS from "aos";
import "aos/dist/aos.css";

const LogIn = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [loader, setLoader] = useState(false);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm({ mode: "onTouched" });

    useEffect(() => {
        AOS.init({ duration: 1500, once: true });
    }, []);

    const loginHandler = async (data) => {
        dispatch(authenticateSignInUser(data, toast, reset, navigate, setLoader));
    };

    const handleGuestLogin = () => {
        const guestCredentials = {
            username: import.meta.env.VITE_GUEST_USERNAME,
            password: import.meta.env.VITE_GUEST_PASSWORD,
        };

        if (!guestCredentials.username || !guestCredentials.password) {
            toast.error("Guest login credentials not found. Please set them in your .env file.");
            return;
        }

        dispatch(authenticateSignInUser(guestCredentials, toast, reset, navigate, setLoader));
    };

    return (
        <div className="min-h-[calc(100vh-64px)] flex justify-center items-center">
            <form
                data-aos="fade-up"
                onSubmit={handleSubmit(loginHandler)}
                className="sm:w-[450px] w-[360px] shadow-custom py-8 sm:px-8 px-4 rounded-md"
            >
                <div className="flex flex-col items-center justify-center space-y-4">
                    <AiOutlineLogin className="text-slate-800 text-5xl" />
                    <h1 className="text-slate-800 text-center font-montserrat lg:text-3xl text-2xl font-bold">
                        Login Here
                    </h1>
                </div>

                <hr className="mt-2 mb-5 text-black" />

                <div className="flex flex-col gap-3">
                    <InputField
                        label="Username"
                        id="username"
                        type="text"
                        required={true}
                        message="*Username is required"
                        placeholder="Enter your username"
                        errors={errors}
                        register={register}
                    />

                    <InputField
                        label="Password"
                        id="password"
                        type="password"
                        required={true}
                        message="*Password is required"
                        placeholder="Enter your password"
                        errors={errors}
                        register={register}
                    />
                </div>

                <button
                    disabled={loader}
                    className="bg-button-gradient flex gap-2 items-center justify-center font-semibold text-white w-full py-2 hover:text-slate-400 transition-colors duration-100 rounded-sm my-3"
                    type="submit"
                >
                    {loader ? (
                        <>
                            <Spinners /> Loading...
                        </>
                    ) : (
                        <>Login</>
                    )}
                </button>

                {/* Guest Login Button */}
                <button
                    type="button"
                    onClick={handleGuestLogin}
                    className="btn-guest"
                >
                    Login as Guest
                </button>

                <p className="text-center text-sm text-slate-700">
                    <span>Don't have an account? </span>
                    <Link
                        className="font-semibold underline hover:text-black"
                        to="/register"
                    >
                        <span>SignUp</span>
                    </Link>
                </p>
            </form>
        </div>
    );
};

export default LogIn;
