# 🛒 E-Kart Frontend

A modern, responsive React e-commerce application built with cutting-edge technologies for optimal user experience.

## ✨ Features

### �️ **E-commerce Core**
- **Product Catalog**: Browse products with advanced search and filtering
- **Product Details**: Comprehensive product information with reviews and ratings
- **Shopping Cart**: Intuitive cart management with real-time updates
- **Checkout Process**: Streamlined order placement with address management
- **Order History**: Track current and past orders with detailed status
- **User Profiles**: Complete profile management and settings

### 🔐 **Authentication & Security**
- **User Registration**: Secure account creation with validation
- **Login System**: JWT-based authentication with auto-refresh
- **Protected Routes**: Route-level authentication guards
- **Role-based Access**: Different UI based on user roles
- **Persistent Sessions**: Secure token management

### 📱 **User Experience**
- **Responsive Design**: Mobile-first approach with Material-UI
- **Progressive Web App**: PWA capabilities for mobile installation
- **Real-time Updates**: Live cart updates and notifications
- **Fast Loading**: Optimized with Vite and React Query caching
- **Error Handling**: Comprehensive error boundaries and user feedback
- **Accessibility**: WCAG compliant components and navigation

### 🎨 **Modern UI/UX**
- **Material Design**: Consistent Material-UI component system
- **Dark/Light Mode**: Theme switching capabilities
- **Micro-interactions**: Smooth animations and transitions
- **Toast Notifications**: Real-time user feedback
- **Loading States**: Skeleton screens and progress indicators
- **Form Validation**: Real-time validation with helpful error messages

## 🚀 Technology Stack

### **Frontend Framework**
- **React 19** - Latest React with concurrent features
- **TypeScript** - Type safety and better developer experience
- **Vite 4.5.0** - Lightning-fast build tool and dev server

### **UI & Styling**
- **Material-UI v7** - Comprehensive React component library
- **Material Icons** - Consistent iconography
- **Responsive Grid** - Mobile-first responsive design
- **CSS-in-JS** - Styled components with emotion

### **State Management**
- **React Query (TanStack Query)** - Server state management and caching
- **Context API** - Global state for authentication and cart
- **useState/useReducer** - Local component state
- **Custom Hooks** - Reusable stateful logic

### **Routing & Navigation**
- **React Router 6.20.0** - Modern declarative routing
- **Protected Routes** - Authentication-based route guards
- **Dynamic Imports** - Code splitting and lazy loading
- **Browser History** - Proper navigation handling

### **Forms & Validation**
- **React Hook Form** - Performant form handling
- **Yup Schema Validation** - Comprehensive form validation
- **Real-time Validation** - Instant user feedback
- **Custom Validators** - Business-specific validation rules

### **HTTP & API Integration**
- **Axios** - HTTP client with interceptors
- **Request/Response Interceptors** - Automatic token handling
- **Error Handling** - Centralized API error management
- **Request Caching** - React Query integration

### **Development Tools**
- **ESLint** - Code linting and style consistency
- **Prettier** - Code formatting
- **React DevTools** - Development debugging
- **Hot Module Replacement** - Instant development feedback

## 📁 Project Architecture

```
ekart-frontend/
├── public/                  # Static assets
│   ├── favicon.ico
│   ├── manifest.json       # PWA manifest
│   └── robots.txt
├── src/
│   ├── components/         # Reusable UI components
│   │   ├── Layout/        # Layout components
│   │   │   ├── Header.jsx # Navigation header
│   │   │   ├── Footer.jsx # Site footer
│   │   │   └── Layout.jsx # Main layout wrapper
│   │   ├── ProductCard.jsx     # Product display card
│   │   ├── CartItem.jsx        # Shopping cart item
│   │   ├── ReviewCard.jsx      # Product review display
│   │   ├── LoadingSpinner.jsx  # Loading indicator
│   │   └── ProtectedRoute.jsx  # Route authentication
│   ├── pages/              # Page-level components
│   │   ├── Home.jsx           # Homepage with featured products
│   │   ├── Products.jsx       # Product catalog with filters
│   │   ├── ProductDetail.jsx  # Individual product page
│   │   ├── Cart.jsx           # Shopping cart page
│   │   ├── Checkout.jsx       # Order checkout process
│   │   ├── Profile.jsx        # User profile management
│   │   ├── Login.jsx          # User authentication
│   │   ├── Register.jsx       # User registration
│   │   └── OrderHistory.jsx   # Order tracking
│   ├── contexts/           # React Context providers
│   │   ├── AuthContext.js     # Authentication context
│   │   ├── AuthProvider.jsx   # Auth provider wrapper
│   │   ├── CartContext.js     # Shopping cart context
│   │   └── CartProvider.jsx   # Cart provider wrapper
│   ├── hooks/              # Custom React hooks
│   │   ├── useAuth.js         # Authentication hook
│   │   ├── useCart.js         # Shopping cart hook
│   │   ├── useProducts.js     # Product data hook
│   │   └── useLocalStorage.js # Local storage hook
│   ├── config/             # Application configuration
│   │   ├── api.js             # API endpoints and client
│   │   ├── theme.js           # Material-UI theme
│   │   └── constants.js       # App constants
│   ├── utils/              # Utility functions
│   │   ├── validators.js      # Form validation schemas
│   │   ├── formatters.js      # Data formatting utilities
│   │   └── helpers.js         # General helper functions
│   ├── styles/             # Global styles
│   │   ├── globals.css        # Global CSS styles
│   │   └── variables.css      # CSS custom properties
│   ├── App.jsx             # Main application component
│   ├── main.jsx            # Application entry point
│   └── index.html          # HTML template
├── package.json            # Dependencies and scripts
├── vite.config.js          # Vite configuration
├── eslint.config.js        # ESLint configuration
└── README.md              # Project documentation
```

## 🏃‍♂️ Quick Start

### **Prerequisites**
- **Node.js 18+** - JavaScript runtime
- **npm 9+** or **yarn 1.22+** - Package manager
- **Modern Browser** - Chrome, Firefox, Safari, Edge

### **Installation & Setup**

1. **Navigate to Frontend Directory**
   ```bash
   cd ekart-frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Environment Configuration**
   ```bash
   # Copy environment template
   cp .env.example .env.local
   
   # Edit environment variables
   VITE_API_BASE_URL=http://localhost:8080
   VITE_KEYCLOAK_URL=http://localhost:8090/realms/ekart
   ```

4. **Start Development Server**
   ```bash
   npm run dev
   # or
   yarn dev
   ```

5. **Open Browser**
   Navigate to `http://localhost:3000`

### **Build for Production**
```bash
# Create production build
npm run build

# Preview production build
npm run preview

# Analyze bundle size
npm run analyze
```

## 🔧 Configuration

### **Environment Variables**
Create a `.env.local` file in the root directory:

```bash
# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_KEYCLOAK_URL=http://localhost:8090/realms/ekart

# Feature Flags
VITE_ENABLE_PWA=true
VITE_ENABLE_ANALYTICS=false
VITE_ENABLE_DARK_MODE=true

# Development Settings
VITE_DEBUG_MODE=true
VITE_MOCK_API=false
```

### **API Integration**
The frontend integrates seamlessly with the backend microservices:

```javascript
// API Endpoints Configuration
const API_ENDPOINTS = {
  // Authentication
  USER_LOGIN: '/api/auth/login',
  USER_REGISTER: '/api/auth/register',
  
  // User Management
  USER_PROFILE: '/api/users/profile',
  
  // Product Catalog
  PRODUCTS: '/api/products',
  PRODUCT_SEARCH: '/api/products/search',
  PRODUCT_CATEGORIES: '/api/products/categories',
  
  // Shopping & Orders
  ORDERS: '/api/orders',
  ORDER_HISTORY: '/api/orders/history',
  
  // Payments
  PAYMENTS: '/api/payments',
  PAYMENT_PROCESS: '/api/payments/process',
  
  // Notifications
  NOTIFICATIONS: '/api/notifications'
};
```

### **Theme Customization**
Material-UI theme configuration with responsive breakpoints:

```javascript
// theme.js
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
      light: '#42a5f5',
      dark: '#1565c0'
    },
    secondary: {
      main: '#dc004e'
    }
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 900,
      lg: 1200,
      xl: 1536
    }
  }
});
```

## 🎨 Component Library

### **Layout Components**
- **Header**: Navigation with cart icon, user menu, search
- **Footer**: Links, contact information, social media
- **Layout**: Main wrapper with responsive sidebar

### **Product Components**
- **ProductCard**: Product display with image, price, ratings
- **ProductDetail**: Comprehensive product information
- **ProductList**: Grid/list view with sorting and filtering
- **ProductSearch**: Advanced search with autocomplete

### **Shopping Components**
- **CartItem**: Individual cart item with quantity controls
- **CartSummary**: Order total calculation and breakdown
- **Checkout**: Multi-step checkout process
- **OrderSummary**: Order confirmation and details

### **User Components**
- **LoginForm**: User authentication with validation
- **RegisterForm**: Account creation with email verification
- **ProfileForm**: User information management
- **AddressBook**: Shipping address management

### **UI Components**
- **LoadingSpinner**: Various loading state indicators
- **ErrorBoundary**: Graceful error handling
- **Toast Notifications**: Real-time user feedback
- **Modal Dialogs**: Confirmation and information dialogs

## 🔄 State Management

### **Authentication State**
```javascript
// AuthContext manages user authentication
const authState = {
  user: null,              // Current user object
  isAuthenticated: false,  // Authentication status
  loading: true,           // Loading state
  login: async (credentials) => {},     // Login function
  logout: () => {},                     // Logout function
  register: async (userData) => {},     // Registration function
  updateUser: (userData) => {}          // Profile update
};
```

### **Shopping Cart State**
```javascript
// CartContext manages shopping cart
const cartState = {
  items: [],               // Cart items array
  totalItems: 0,           // Total item count
  totalPrice: 0,           // Total cart value
  addItem: (product) => {},            // Add product to cart
  removeItem: (productId) => {},       // Remove product
  updateQuantity: (productId, qty) => {}, // Update quantity
  clearCart: () => {}                  // Clear entire cart
};
```

### **Server State Management**
React Query handles all server state with caching and synchronization:

```javascript
// Product queries
const { data: products, isLoading, error } = useQuery({
  queryKey: ['products', { page, category, search }],
  queryFn: () => fetchProducts({ page, category, search }),
  staleTime: 5 * 60 * 1000, // 5 minutes
});

// Order mutations
const orderMutation = useMutation({
  mutationFn: createOrder,
  onSuccess: () => {
    queryClient.invalidateQueries(['orders']);
    navigate('/order-confirmation');
  }
});
```

## 📱 Responsive Design

### **Breakpoint Strategy**
- **Mobile First**: Design starts from smallest screen
- **Progressive Enhancement**: Features added for larger screens
- **Touch Friendly**: Large touch targets and gestures
- **Performance Optimized**: Smaller images and code splitting for mobile

### **Responsive Components**
```javascript
// Responsive grid system
<Grid container spacing={{ xs: 1, sm: 2, md: 3 }}>
  <Grid item xs={12} sm={6} md={4} lg={3}>
    <ProductCard product={product} />
  </Grid>
</Grid>

// Responsive typography
<Typography 
  variant="h4" 
  sx={{ 
    fontSize: { xs: '1.5rem', sm: '2rem', md: '2.5rem' } 
  }}
>
  Page Title
</Typography>
```

### **Mobile Optimizations**
- **Navigation**: Collapsible mobile menu
- **Forms**: Touch-friendly inputs with proper keyboard types
- **Images**: Responsive images with srcSet
- **Performance**: Lazy loading and code splitting

## 🔍 Performance Optimization

### **Code Splitting**
```javascript
// Lazy loading of route components
const Home = lazy(() => import('./pages/Home'));
const Products = lazy(() => import('./pages/Products'));
const ProductDetail = lazy(() => import('./pages/ProductDetail'));

// Suspense boundaries for loading states
<Suspense fallback={<LoadingSpinner />}>
  <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/products" element={<Products />} />
  </Routes>
</Suspense>
```

### **Image Optimization**
- **Lazy Loading**: Images load as they enter viewport
- **WebP Format**: Modern image format for better compression
- **Responsive Images**: Multiple sizes for different devices
- **Placeholder Loading**: Skeleton screens during image load

### **Bundle Optimization**
- **Tree Shaking**: Unused code elimination
- **Chunk Splitting**: Vendor and app code separation
- **Compression**: Gzip and Brotli compression
- **Caching**: Aggressive caching strategies

## 🧪 Testing Strategy

### **Testing Tools**
- **Vitest**: Unit and integration testing
- **React Testing Library**: Component testing
- **MSW**: API mocking for tests
- **Playwright**: End-to-end testing

### **Test Categories**
```javascript
// Unit tests for utilities
describe('formatPrice', () => {
  it('formats currency correctly', () => {
    expect(formatPrice(1234.56)).toBe('$1,234.56');
  });
});

// Component tests
test('ProductCard displays product information', async () => {
  render(<ProductCard product={mockProduct} />);
  expect(screen.getByText(mockProduct.name)).toBeInTheDocument();
  expect(screen.getByText(`$${mockProduct.price}`)).toBeInTheDocument();
});

// Integration tests
test('Shopping cart workflow', async () => {
  render(<App />);
  // Add product to cart
  // Verify cart updates
  // Proceed to checkout
  // Verify order creation
});
```

## 🚀 Deployment

### **Development Deployment**
```bash
# Local development server
npm run dev

# Local preview of production build
npm run build && npm run preview
```

### **Production Deployment**

#### **Static Hosting (Netlify, Vercel)**
```bash
# Build for production
npm run build

# Deploy dist folder to static hosting
# Configure redirects for SPA routing
```

#### **Docker Deployment**
```dockerfile
# Dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### **CDN Integration**
- **Asset Optimization**: Automatic image and asset optimization
- **Global Distribution**: CDN for worldwide performance
- **Cache Headers**: Proper caching strategies
- **Service Worker**: Offline capabilities

## 🔒 Security Best Practices

### **Authentication Security**
- **JWT Tokens**: Secure storage in httpOnly cookies (recommended)
- **Token Refresh**: Automatic token renewal
- **Route Protection**: Client-side route guards
- **CSRF Protection**: Cross-site request forgery prevention

### **Data Security**
- **Input Validation**: Client-side and server-side validation
- **XSS Prevention**: Sanitized user inputs
- **HTTPS Only**: Secure communication
- **Content Security Policy**: CSP headers for additional security

### **API Security**
- **Request Interceptors**: Automatic token attachment
- **Error Handling**: Secure error messages
- **Rate Limiting**: Client-side request throttling
- **CORS Configuration**: Proper cross-origin setup

## 🎯 Browser Support

### **Supported Browsers**
- **Chrome**: 90+
- **Firefox**: 88+
- **Safari**: 14+
- **Edge**: 90+
- **Mobile Safari**: iOS 14+
- **Chrome Mobile**: Android 8+

### **Progressive Enhancement**
- **Core Features**: Work on all supported browsers
- **Enhanced Features**: Advanced features for modern browsers
- **Polyfills**: Automatic polyfills for missing features
- **Graceful Degradation**: Fallbacks for unsupported features

## 🤝 Contributing

### **Development Guidelines**
1. **Code Style**: Follow ESLint and Prettier configurations
2. **Component Design**: Create reusable, accessible components
3. **Testing**: Write tests for new features and bug fixes
4. **Documentation**: Update README and component documentation
5. **Performance**: Consider performance implications of changes

### **Pull Request Process**
1. **Feature Branch**: Create branch from main
2. **Development**: Implement feature with tests
3. **Testing**: Ensure all tests pass
4. **Documentation**: Update relevant documentation
5. **Review**: Submit PR for code review

## 📚 Resources & Documentation

### **Learning Resources**
- **React Documentation**: https://react.dev/
- **Material-UI Documentation**: https://mui.com/
- **Vite Documentation**: https://vitejs.dev/
- **React Query Documentation**: https://tanstack.com/query/

### **Design System**
- **Material Design**: https://material.io/design
- **Accessibility Guidelines**: https://www.w3.org/WAI/WCAG21/
- **Performance Best Practices**: https://web.dev/performance/

---

**🎨 Build Beautiful E-commerce Experiences with E-Kart Frontend!**

For backend integration and API documentation, see the main [E-Kart Backend README](../README.md).

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm or yarn

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd ekart-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to `http://localhost:5173`

### Environment Configuration

The frontend connects to the backend services through the API Gateway at `http://localhost:8080`. Make sure your backend services are running:

1. Start infrastructure services:
   ```bash
   cd .. && docker-compose up -d
   ```

2. Start Spring Boot services (config-server, eureka-server, api-gateway, etc.)

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## API Integration

The frontend integrates with the following microservices through the API Gateway:

- **User Service** (`/api/users`) - Authentication and user management
- **Product Service** (`/api/products`) - Product catalog and search
- **Order Service** (`/api/orders`) - Order management
- **Payment Service** (`/api/payments`) - Payment processing
- **Notification Service** (`/api/notifications`) - Notifications

## Authentication Flow

1. User registers/logs in through the frontend
2. Backend returns JWT access and refresh tokens
3. Tokens are stored in localStorage
4. API client automatically includes tokens in requests
5. Automatic token refresh and logout on expiry

## State Management

- **Authentication**: Context API with useAuth hook
- **Shopping Cart**: Context API with localStorage persistence
- **Server State**: React Query for caching and synchronization
- **Local State**: React useState and useReducer

## Responsive Design

The application is fully responsive and works on:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (320px - 767px)

## Production Build

To build for production:

```bash
npm run build
```

The built files will be in the `dist` directory and can be served by any static file server.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is part of the E-Kart microservices application.
