import { FaGithubSquare, FaLinkedin } from "react-icons/fa";

const Footer = () => {
    return (
        <footer className="bg-slate-900 text-white text-center py-6 mt-10">
            <div className="flex justify-center space-x-6 mb-2">
                <a
                    href="https://www.linkedin.com/in/callan-maguire"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="hover:text-blue-400 transition"
                >
                    <FaLinkedin size={28} />
                </a>
                <a
                    href="https://github.com/Callymags/sb-bucketlist"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="hover:text-gray-400 transition"
                >
                    <FaGithubSquare size={28} />
                </a>
            </div>
            <p className="text-sm text-gray-300">&copy; Bucket List 2025</p>
        </footer>
    );
};

export default Footer;
