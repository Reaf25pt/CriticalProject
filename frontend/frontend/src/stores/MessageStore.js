import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

// define the store

export const messageStore = create(
  persist(
    (set) => ({
      messages: [],
      updateMessages: (messages) => set({ messages }),
      addMessages: (newMessage) =>
        set((state) => ({
          messages: [...state.messages, newMessage],
        })),
    }),
    {
      name: "messages-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
