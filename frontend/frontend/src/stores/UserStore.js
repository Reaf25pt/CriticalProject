import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

// define the store

export const userStore = create(
  persist(
    (set) => ({
      user: null,
      setUser: (newUser) => set({ user: newUser }),
      updateUser: (key, value) =>
        set((state) => ({ user: { ...state.user, [key]: value } })),
      clearLoggedUser: () => set({ user: null }),
    }),
    {
      name: "user-profile-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
