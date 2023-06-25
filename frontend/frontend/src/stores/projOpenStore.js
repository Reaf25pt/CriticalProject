import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

// define the store

export const projOpenStore = create(
  persist(
    (set) => ({
      project: null,
      setProjOpen: (newProj) => set({ project: newProj }),
      updateProj: (key, value) =>
        set((state) => ({ project: { ...state.project, [key]: value } })),
      clearProject: () => set({ project: null }),
    }),
    {
      name: "project-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
