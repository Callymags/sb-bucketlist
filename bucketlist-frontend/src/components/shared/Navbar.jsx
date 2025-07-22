import { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { IoIosMenu } from "react-icons/io";
import { RxCross2 } from "react-icons/rx";
import logo from "../../assets/images/bucket-brand.png";
import { logOutUser } from "../../store/actions";

const Navbar = () => {
    const [navbarOpen, setNavbarOpen] = useState(false);
    const path = useLocation().pathname;
    const { user } = useSelector((state) => state.auth);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // Close navbar dropdown on route change
    useEffect(() => {
        setNavbarOpen(false);
    }, [path]);

    const handleLogout = () => {
        dispatch(logOutUser(navigate));
    };

    return (
        <div className="h-[70px] bg-custom-gradient text-white z-50 flex items-center sticky top-0">
            <div className="w-full flex justify-between items-center px-4 sm:px-8 lg:px-14">
                {/* Logo */}
                <div className="flex items-center gap-2 text-white text-xl font-bold cursor-default select-none">
                    <img src={logo} alt="Bucket List Logo" className="h-8 w-8 object-contain" />
                    Bucket List
                </div>

                {/* Nav Links */}
                <ul
                    className={`sm:flex sm:gap-10 items-center sm:static absolute left-0 top-[70px] sm:shadow-none shadow-md
        ${navbarOpen ? "h-fit pt-4 pb-4" : "h-0 overflow-hidden"}
        sm:h-fit sm:bg-transparent bg-slate-900 text-white sm:w-auto w-full px-4 sm:px-0 transition-all duration-300`}
                >
                    {!user?.id && (
                        <li className="py-2 sm:py-0">
                            <Link
                                to="/"
                                className={`block w-full px-2 py-2 ${
                                    path === "/" ? "text-white font-semibold" : "text-gray-200"
                                } hover:text-white text-start`}
                            >
                                Home
                            </Link>
                        </li>
                    )}

                    {!user?.id ? (
                        <>
                            <li className="py-2 sm:py-0">
                                <Link
                                    to="/login"
                                    className="block w-full px-2 py-2 text-gray-200 hover:text-white text-start"
                                >
                                    Login
                                </Link>
                            </li>
                            <li className="py-2 sm:py-0">
                                <Link
                                    to="/register"
                                    className="block w-full px-2 py-2 text-gray-200 hover:text-white text-start"
                                >
                                    Register
                                </Link>
                            </li>
                        </>
                    ) : (
                        <>
                            <li className="py-2 sm:py-0">
                                <Link
                                    to="/experiences"
                                    className={`block w-full px-2 py-2 ${
                                        path === "/experiences" ? "text-white font-semibold" : "text-gray-200"
                                    } hover:text-white text-start`}
                                >
                                    Explore
                                </Link>
                            </li>

                            <li className="py-2 sm:py-0">
                                <Link
                                    to="/experiences/create"
                                    className="block w-full px-2 py-2 text-gray-200 hover:text-white text-start"
                                >
                                    Create Experience
                                </Link>
                            </li>

                            <li className="py-2 sm:py-0">
                                <Link
                                    to="/profile"
                                    className={`block w-full px-2 py-2 ${
                                        path === "/profile" ? "text-white font-semibold" : "text-gray-200"
                                    } hover:text-white text-start`}
                                >
                                    Profile
                                </Link>
                            </li>

                            <li className="py-2 sm:py-0">
                                <button
                                    onClick={handleLogout}
                                    className="block w-full px-2 py-2 text-gray-200 hover:text-white text-start"
                                >
                                    Logout
                                </button>
                            </li>
                        </>
                    )}
                </ul>

                {/* Mobile Menu Toggle */}
                <button onClick={() => setNavbarOpen(!navbarOpen)} className="sm:hidden flex items-center">
                    {navbarOpen ? (
                        <RxCross2 className="text-white text-3xl" />
                    ) : (
                        <IoIosMenu className="text-white text-3xl" />
                    )}
                </button>
            </div>
        </div>
    );
};

export default Navbar;
