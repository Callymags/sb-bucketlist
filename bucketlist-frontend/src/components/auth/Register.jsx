import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { FaUserPlus } from 'react-icons/fa';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../shared/InputField';
import { useDispatch } from 'react-redux';
import { registerNewUser } from '../../store/actions';
import toast from 'react-hot-toast';
import Spinners from '../shared/Spinners';
import RegisterSuccessModal from '../shared/RegisterSuccessModal';
import AOS from "aos";
import "aos/dist/aos.css";

const Register = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [loader, setLoader] = useState(false);
    const [showModal, setShowModal] = useState(false);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm({ mode: "onTouched" });

    useEffect(() => {
        AOS.init({ duration: 1500, once: true });
    }, []);

    const registerHandler = async (data) => {
        setLoader(true);
        const success = await dispatch(registerNewUser(data, toast, reset, setLoader));
        if (success) {
            setShowModal(true);
        }
    };

    const handleModalClose = () => {
        setShowModal(false);
        navigate("/login");
    };

    return (
        <>
            <div className="min-h-[calc(100vh-64px)] flex justify-center items-center">
                <form
                    data-aos="fade-up"
                    onSubmit={handleSubmit(registerHandler)}
                    className="sm:w-[450px] w-[360px] shadow-custom py-8 sm:px-8 px-4 rounded-md"
                >
                    <div className="flex flex-col items-center justify-center space-y-4">
                        <FaUserPlus className="text-slate-800 text-5xl" />
                        <h1 className="text-slate-800 text-center font-montserrat lg:text-3xl text-2xl font-bold">
                            Register Here
                        </h1>
                    </div>
                    <hr className="mt-2 mb-5 text-black" />
                    <div className="flex flex-col gap-3">
                        <InputField
                            label="Username"
                            required
                            id="username"
                            type="text"
                            message="*Username is required"
                            placeholder="Enter your username"
                            register={register}
                            errors={errors}
                        />

                        <InputField
                            label="Email"
                            required
                            id="email"
                            type="email"
                            message="*Email is required"
                            placeholder="Enter your email"
                            register={register}
                            errors={errors}
                        />

                        <InputField
                            label="Password"
                            required
                            id="password"
                            min={6}
                            type="password"
                            message="*Password is required"
                            placeholder="Enter your password"
                            register={register}
                            errors={errors}
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
                            <>Register</>
                        )}
                    </button>

                    <p className="text-center text-sm text-slate-700 mt-6">
                        <span>Already have an account? </span>
                        <Link className="font-semibold underline hover:text-black" to="/login">
                            <span>Login</span>
                        </Link>
                    </p>
                </form>
            </div>

            <RegisterSuccessModal
                show={showModal}
                message="Registration successful! You will now be redirected to the login screen."
                onClose={handleModalClose}
            />
        </>
    );
};

export default Register;
