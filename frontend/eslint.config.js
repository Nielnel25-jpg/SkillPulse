import js from '@eslint/js';
import globals from 'globals';
import reactPlugin from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';

export default [
  { ignores: ['dist', 'node_modules'] },

  // ESLint JS recommended base
  js.configs.recommended,

  // JS / JSX source files
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: { ...globals.browser },
      parserOptions: {
        ecmaFeatures: { jsx: true },
      },
    },
    settings: {
      react: { version: 'detect' },
    },
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      // React flat config recommended (ESLint 9/10 compatible)
      ...reactPlugin.configs.flat.recommended.rules,

      // React Hooks
      ...reactHooks.configs['recommended-latest'].rules,

      // Fast Refresh — warn when a file exports something that isn't a component
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],

      // React 17+ JSX transform — no need to import React in every file
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',

      // Style preferences
      'no-unused-vars': ['warn', { varsIgnorePattern: '^_', argsIgnorePattern: '^_' }],
      'no-console': ['warn', { allow: ['warn', 'error'] }],
    },
  },
];
