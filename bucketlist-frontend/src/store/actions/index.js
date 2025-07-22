import api from "../../api/api";

// Action Types
export const EXPERIENCE_LOADING = "EXPERIENCE_LOADING";
export const FETCH_EXPERIENCES = "FETCH_EXPERIENCES";
export const FETCH_SINGLE_EXPERIENCE = "FETCH_SINGLE_EXPERIENCE";
export const EXPERIENCE_ERROR = "EXPERIENCE_ERROR";

// Fetch All Experiences with optional filters
export const fetchExperiences = (
    { page = 0, size = 10, sortBy = "experienceId", sortOrder = "desc", categoryId, keyword } = {}
) => async (dispatch) => {
    try {
        dispatch({ type: EXPERIENCE_LOADING });

        let url = `/experiences`; // Default endpoint
        if (keyword) {
            url = `/experiences/keyword/${keyword}`;
        } else if (categoryId) {
            url = `/categories/${categoryId}/experiences`;
        }

        const params = { pageNumber: page, pageSize: size, sortBy, sortOrder };

        const { data } = await api.get(url, { params });

        dispatch({
            type: FETCH_EXPERIENCES,
            payload: {
                experiences: data.content,
                pageNumber: data.pageNumber,
                pageSize: data.pageSize,
                totalElements: data.totalElements,
                totalPages: data.totalPages,
                lastPage: data.lastPage,
            },
        });
    } catch (error) {
        console.error(error);

        let message = "Failed to fetch experiences";

        // if pageNumber is too high and backend returns 400
        if (error?.response?.status === 400) {
            message = "You've gone beyond the available pages. Please return to page 1.";
        } else if (error?.response?.data?.message) {
            message = error.response.data.message;
        }

        dispatch({
            type: EXPERIENCE_ERROR,
            payload: message,
        });
    }
};

export const fetchSingleExperience = (id) => async (dispatch) => {
    try {
        dispatch({ type: EXPERIENCE_LOADING });

        const { data } = await api.get(`/experiences/${id}`);

        dispatch({
            type: FETCH_SINGLE_EXPERIENCE,
            payload: data,
        });
    } catch (error) {
        dispatch({
            type: EXPERIENCE_ERROR,
            payload: error?.response?.data?.message || "Failed to load experience",
        });
    }
};

export const fetchUserExperiences = (userId, page = 0, size = 8) => async (dispatch) => {
    try {
        const params = {
            pageNumber: page,
            pageSize: size,
            sortBy: "experienceId",
            sortOrder: "desc",
        };

        const { data } = await api.get(`/users/${userId}/experiences`, { params });

        dispatch({ type: "FETCH_USER_EXPERIENCES", payload: data });
    } catch (err) {
        console.error("Failed to fetch created experiences", err);
    }
};


export const fetchUserBucketList = (userId) => async (dispatch) => {
    try {
        const { data } = await api.get(`/bucketLists/users/bucketList`);
        dispatch({ type: "FETCH_USER_BUCKETLIST", payload: data });
    } catch (err) {
        console.error("Failed to fetch bucket list", err);
    }
};

export const fetchCategories = () => async (dispatch) => {
    try {
        dispatch({ type: "CATEGORY_LOADER" });
        const { data } = await api.get(`/categories`);
        dispatch({
            type: "FETCH_CATEGORIES",
            payload: data.content,
            pageNumber: data.pageNumber,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({ type: "IS_ERROR", payload: null });
    } catch (error) {
        console.log(error);
        dispatch({
            type: "IS_ERROR",
            payload: error?.response?.data?.message || "Failed to fetch categories",
        });
    }
};

// Auth actions
export const authenticateSignInUser = (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
    try {
        setLoader(true);
        const { data } = await api.post("/auth/signIn", sendData, { withCredentials: true });
        console.log("🚀 Login API response:", data);
        dispatch({
            type: "LOGIN_USER",
            payload: {
                id: data.userId,
                username: data.username,
                roles: data.roles
            }
        });
        localStorage.setItem("auth", JSON.stringify({
            id: data.userId,
            username: data.username,
            roles: data.roles
        }));
        reset();
        toast.success("Login Success");
        navigate("/profile");
    } catch (error) {
        console.log(error);
        toast.error(error?.response?.data?.message || "Internal Server Error");
    } finally {
        setLoader(false);
    }
};

export const registerNewUser = (data, toast, reset, setLoader) => async (dispatch) => {
    try {
        const response = await api.post(`/auth/signUp`, data);
        toast.success("Registration successful!");
        reset();
        setLoader(false);
        return true;
    } catch (error) {
        const backendMessage =
            error?.response?.data?.message?.replace(/^Error:\s*/, '') || "Something went wrong. Please try again.";
        toast.error(backendMessage);
        setLoader(false);
        return false;
    }
};



export const logOutUser = (navigate) => async (dispatch) => {
    try {
        await api.post("/auth/signOut"); // clear the cookie
    } catch (err) {
        console.error("Logout failed", err);
    } finally {
        dispatch({ type: "LOG_OUT" });
        localStorage.removeItem("auth");
        navigate("/login");
    }
};