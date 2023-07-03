import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

// define the store

export const notificationStore = create(
  persist(
    (set) => ({
      notifications: [],
      updateNotifications: (notifications) => set({ notifications }),
      addNotification: (newNotification) =>
        set((state) => ({
          notifications: [newNotification, ...state.notifications], // adiciona at the top a última notificação
        })),
      filterNotification: (newNotification) =>
        set((state) => ({
          notifications: state.notifications.map((item) =>
            item.id === newNotification.notificationId ? newNotification : item
          ),
        })),

      clearNotifications: () => set({ notifications: [] }),
    }),
    {
      name: "notifications-store", // the name to use for the persisted data
      storage: createJSONStorage(() => sessionStorage), // the storage mechanism to use (sessionStorage or  localStorage)
    }
  )
);
