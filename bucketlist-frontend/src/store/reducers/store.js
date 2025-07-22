import { configureStore } from "@reduxjs/toolkit";
import { experienceReducer } from "./experienceReducer.js";
import { errorReducer } from "./errorReducer";
import { bucketListReducer } from "./bucketListReducer.js";
import { authReducer } from "./authReducer";
import {profileReducer} from "./profileReducer.js";
import {categoryReducer} from "./categoryReducer.js";

const user = localStorage.getItem("auth")
    ? JSON.parse(localStorage.getItem("auth"))
    : null;

const bucketListExps = localStorage.getItem("bucketListExps")
    ? JSON.parse(localStorage.getItem("bucketListExps"))
    : [];


const initialState = {
    auth: { user: user },
    bucketList: { bucketListExps: bucketListExps, bucketListId: null },
};

export const store = configureStore({
    reducer: {
        experiences: experienceReducer,
        categories: categoryReducer,
        errors: errorReducer,
        profile: profileReducer,
        bucketList: bucketListReducer,
        auth: authReducer,
    },
    preloadedState: initialState,
    devTools: true,
});

export default store;
