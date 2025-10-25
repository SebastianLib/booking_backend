import { UserRole } from "src/enums/user-role.enum";

export type JwtUser = {
    userId: number;
    role: UserRole;
}