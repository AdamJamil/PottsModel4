package com.company;

class Node
{
    private Node left = null, right = null;

    boolean contains(boolean[] upset)
    {
        boolean result = true;
        Node ptr = this;

        for (boolean b : upset)
        {
            if (b)
            {
                if (ptr.right == null)
                {
                    ptr.right = new Node();
                    result = false;
                }

                ptr = ptr.right;

                continue;
            }

            if (ptr.left == null)
            {
                ptr.left = new Node();
                result = false;
            }

            ptr = ptr.left;
        }

        return result;
    }
}
