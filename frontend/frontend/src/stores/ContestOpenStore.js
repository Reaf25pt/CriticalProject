import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

// define the store

export const contestOpenStore = create(
  persist(
    (set) => ({
      contest: null,
      setContestOpen: (newContest) => set({ contest: newContest }),
      updateContest: (key, value) =>
        set((state) => ({ contest: { ...state.contest, [key]: value } })),
      clearContest: () => set({ contest: null }),
    }),
    {
      name: "contest-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
