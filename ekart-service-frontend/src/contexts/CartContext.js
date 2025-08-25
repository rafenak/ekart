import { createContext, useContext } from 'react';

export const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  
  // Map the context values to match expected interface
  return {
    cart: context.cartItems,
    cartItems: context.cartItems,
    totalAmount: context.totalAmount,
    itemCount: context.itemCount,
    addToCart: context.addToCart,
    removeFromCart: context.removeFromCart,
    updateQuantity: context.updateQuantity,
    clearCart: context.clearCart,
    isInCart: context.isInCart,
    getItemQuantity: context.getItemQuantity,
    getTotalPrice: () => context.totalAmount, // Function version for compatibility
  };
};
