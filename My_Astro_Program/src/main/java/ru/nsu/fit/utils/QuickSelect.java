package ru.nsu.fit.utils;

public class QuickSelect {
    private QuickSelect() {
        throw new IllegalStateException("Utility class");
    }

    // Метод для поиска k-го наибольшего элемента (QuickSelect)
    public static int findKthLargest(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, k);
    }

    private static int quickSelect(int[] nums, int low, int high, int k) {
        if (low == high) {
            return nums[low];
        }

        int pivotIndex = partition(nums, low, high);
        if (k == pivotIndex) {
            return nums[k];
        } else if (k < pivotIndex) {
            return quickSelect(nums, low, pivotIndex - 1, k);
        } else {
            return quickSelect(nums, pivotIndex + 1, high, k);
        }
    }

    private static int partition(int[] nums, int low, int high) {
        int pivot = nums[high];
        int i = low;
        for (int j = low; j < high; j++) {
            if (nums[j] <= pivot) {
                swap(nums, i, j);
                i++;
            }
        }
        swap(nums, i, high);
        return i;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}