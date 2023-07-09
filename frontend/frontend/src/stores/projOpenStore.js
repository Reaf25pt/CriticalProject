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

      chatMessages: [],
      updateChatMessages: (chatMessages) => set({ chatMessages }),
      addChatMessage: (newChatMessage) =>
        set((state) => ({
          chatMessages: [...state.chatMessages, newChatMessage],
        })),

      members: [],
      setMembers: (membersList) => set({ members: membersList }),

      pendingInvites: [],
      setPendingInvites: (pendingList) => set({ pendingInvites: pendingList }),

      tasks: [],
      setTasks: (tasksList) => set({ tasks: tasksList }),
    }),

    {
      name: "project-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
