import { Pagination } from "@mui/material";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Paginations = ({ numberOfPage, currentPage, onPageChange }) => {
    const [searchParams] = useSearchParams();
    const pathname = useLocation().pathname;
    const navigate = useNavigate();
    const params = new URLSearchParams(searchParams);

    const onChangeHandler = (event, value) => {
        if (onPageChange) {
            onPageChange(value); // For private pages (e.g. Profile)
        } else {
            params.set("page", value.toString()); // For public pages (e.g. Explore)
            navigate(`${pathname}?${params}`);
        }
    };

    return (
        <Pagination
            count={numberOfPage}
            page={currentPage}
            shape="rounded"
            onChange={onChangeHandler}
            siblingCount={1}
            boundaryCount={1}
        />
    );
};

export default Paginations;
